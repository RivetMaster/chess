package WebSocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.GameDAO;
import exceptions.DataAccessException;
import exceptions.InvalidAuthTokenException;
import exceptions.InvalidRequestException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import resultsandrequests.JoinGameRequest;
import service.AuthService;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameMove;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

import java.io.IOException;

import static chess.ChessGame.GameStatus.*;
import static chess.ChessGame.TeamColor.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO;
    private final GameService gameService;
    private final AuthService authService;

    public WebSocketHandler(GameDAO gameDAO, GameService gameService, AuthService authService){
        super();
        this.gameDAO = gameDAO;
        this.gameService = gameService;
        this.authService = authService;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (action.getCommandType()) {
                case CONNECT -> {
                    //verify user
                    if(authService.verifyAuth(action.getAuthToken())) {
                        String username = authService.getUsername(action.getAuthToken());
                        connect(action.getGameID(), username, ctx.session);
                    }
                }
                case MAKE_MOVE -> {
                    if(authService.verifyAuth(action.getAuthToken())) {
                        String username = authService.getUsername(action.getAuthToken());

                        if(username.equals(gameDAO.getGame(action.getGameID()).blackUsername())) {
                            UserGameMove command = new Gson().fromJson(ctx.message(), UserGameMove.class);
                            makeMove(command, username, BLACK, ctx.session);
                        } else if(username.equals(gameDAO.getGame(action.getGameID()).whiteUsername())){
                            UserGameMove command = new Gson().fromJson(ctx.message(), UserGameMove.class);
                            makeMove(command, username, WHITE, ctx.session);
                        }else {
                            connections.send(new ErrorMessage("Error: Not Player in Game"), ctx.session);
                        }
                    }
                }
                case LEAVE -> {
                    AuthData auth = new AuthData(action.getAuthToken(), authService.getUsername(action.getAuthToken()));
                    ChessGame.TeamColor color = null;
                    if (authService.getUsername(action.getAuthToken()).equals(gameDAO.getGame(action.getGameID()).whiteUsername())) {
                        color = WHITE;
                    } else if(authService.getUsername(action.getAuthToken()).equals(gameDAO.getGame(action.getGameID()).blackUsername())){
                        color = BLACK;
                    }
                    if(color == null){
                        throw new InvalidRequestException("Error: Player not in game");
                    }
                    JoinGameRequest request = new JoinGameRequest(action.getGameID(), color, auth);
                    leave(request, ctx.session);
                }
                case RESIGN -> {
                    String username = authService.getUsername(action.getAuthToken());
                    String opponent = gameDAO.getGame(action.getGameID()).whiteUsername();
                    if(username.equals(opponent)) {
                        opponent = gameDAO.getGame(action.getGameID()).blackUsername();
                    }
                    resign(username, opponent, action.getGameID(), ctx.session);
                }
            }
        }
        catch (InvalidAuthTokenException e){
            //send error message
            try {
                connections.send(new ErrorMessage("Error: Not Authorized"), ctx.session);
            } catch(IOException ex){
                ex.printStackTrace();
            }
        } catch (DataAccessException e){
            try {
                connections.send(new ErrorMessage(String.format("Error: %s", e.getMessage())), ctx.session);
            } catch(IOException ex){
                ex.printStackTrace();
            }
        } catch (InvalidMoveException e){
            //send error message
            try {
                connections.send(new ErrorMessage(String.format("Error: %s", e.getMessage())), ctx.session);
            }catch(IOException ex){
                ex.printStackTrace();
            }
        } catch (InvalidRequestException e){
            //send error message
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(int gameID, String username, Session session) throws IOException, InvalidRequestException, DataAccessException {
        //send load game message
        try {
            ChessGame gameMessage = gameDAO.getGame(gameID).game();
            LoadGame loadGame = new LoadGame(gameMessage);
            connections.add(session, gameID);
            connections.send(loadGame, session);

            //check if user in the game, if in the game send joined as a player, if not joined as observer
            String message;
            if(gameDAO.getGame(gameID).whiteUsername().equals(username) || gameDAO.getGame(gameID).blackUsername().equals(username)){
                message = String.format("%s joined the game", username);
            } else{
                message = String.format("%s started watching the game", username);
            }
            Notification notification = new Notification(message);
            connections.broadcast(session, notification, gameID);
        } catch (InvalidRequestException e){
            String message = String.format("Error: %s", e.getMessage());
            ErrorMessage error = new ErrorMessage(message);
            connections.send(error, session);
        }
    }

    private void leave(JoinGameRequest req, Session session) throws IOException, InvalidRequestException, DataAccessException, InvalidAuthTokenException {
        gameService.leaveGame(req);
        var message = String.format("%s left the game", req.auth().username());
        var notification = new Notification(message);
        connections.broadcast(session, notification, req.gameID());
        connections.remove(session, req.gameID());
    }

    public void makeMove(UserGameMove command, String username, ChessGame.TeamColor color, Session session) throws IOException, DataAccessException, InvalidMoveException, InvalidRequestException {
        //check that can make move, turn, both players in game
        GameData gameData = gameDAO.getGame(command.getGameID());
        ChessGame game = gameData.game();
        gameData.setGameStatus();

        if(game.getStatus().equals(WAITING)){
            connections.send(new ErrorMessage("Error: Game needs two players to play game"), session);
        } else if (game.getStatus().equals(GAME_OVER)){
            connections.send(new ErrorMessage("Error: Game Over"), session);
        }else if(!game.getTeamTurn().equals(color)){
            connections.send(new ErrorMessage("Error: Can only move on your turn"), session);
        } else if(!game.getBoard().getPiece(command.getMove().getStartPosition()).getTeamColor().equals(color)) {
            connections.send(new ErrorMessage("Error: Can only move your pieces"), session);
        }
        else {
            //make move
            game.makeMove(command.getMove());
            GameData updatedGame = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.updateGame(command.getGameID(), updatedGame);
            //send load game message
            LoadGame loadGameMessage = new LoadGame(updatedGame.game());
            connections.broadcast(null, loadGameMessage, command.getGameID());

            //send made move message
            String message = String.format("%s makes move %s", username, command.getMove().moveString());
            Notification notification = new Notification(message);
            connections.broadcast(session, notification, command.getGameID());

            //if move resulted in check, checkmate, or stalemate for opponent, send message to all
            message = null;
            if(color == WHITE){
                if(game.isInCheckmate(BLACK)){
                    message = String.format("%s is in checkmate\n%s Wins!", gameData.blackUsername(), username);
                } else if (game.isInCheck(BLACK)){
                    message = String.format("%s is in check", gameData.blackUsername());
                } else if(game.isInStalemate(BLACK)){
                    message = String.format("%s is in stalemate\nGame Over!", gameData.blackUsername());
                }
            }
            if(color == BLACK){
                if(game.isInCheckmate(WHITE)){
                    message = String.format("%s is in checkmate\n%s Wins!", gameData.whiteUsername(), username);
                } else if (game.isInCheck(WHITE)){
                    message = String.format("%s is in check", gameData.whiteUsername());
                } else if(game.isInStalemate(WHITE)){
                    message = String.format("%s is in stalemate\nGame Over!", gameData.whiteUsername());
                }
            }
            if(message != null){
                Notification update = new Notification(message);
                connections.broadcast(null, update, command.getGameID());
            }
        }
    }

    private void resign(String username, String opponent, int id, Session session) throws IOException {
        var message = String.format("%s resigned. %s wins!", username, opponent);
        var notification = new Notification(message);
        connections.broadcast(session, notification, id);
    }
}