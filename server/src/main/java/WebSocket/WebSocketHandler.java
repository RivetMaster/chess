package WebSocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.GameDAO;
import exceptions.DataAccessException;
import exceptions.InvalidAuthTokenException;
import exceptions.InvalidRequestException;
import exceptions.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import resultsandrequests.JoinGameRequest;
import service.AuthService;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameMove;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

import java.io.IOException;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

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
                case CONNECT -> connect(action.getGameID(), action.getAuthToken(), ctx.session);
                case MAKE_MOVE -> {
                    UserGameMove command = new Gson().fromJson(ctx.message(), UserGameMove.class);
                    String username = authService.getUsername(command.getAuthToken());
                    makeMove(command, username, ctx.session);
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
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InvalidAuthTokenException e){
            //send error message
        } catch (DataAccessException e){
            //send error message
        } catch (InvalidMoveException e){
            //send error message
        } catch (InvalidRequestException e){
            //send error message
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(int gameID, String username, Session session) throws IOException {
        connections.add(session, gameID);
        var message = String.format("%s joined the game", username); //no bc called when join and when observe
        var notification = new Notification(message);
        connections.broadcast(session, notification, gameID);
    }

    private void leave(JoinGameRequest req, Session session) throws IOException, InvalidRequestException, DataAccessException, InvalidAuthTokenException {
        gameService.leaveGame(req);
        var message = String.format("%s left the game", req.auth().username());
        var notification = new Notification(message);
        connections.broadcast(session, notification, req.gameID());
        connections.remove(session, req.gameID());
    }

    public void makeMove(UserGameMove command, String username, Session session) throws IOException, DataAccessException, InvalidMoveException, InvalidRequestException {
        //check that can make move, turn, both players in game
        GameData gameData = gameDAO.getGame(command.getGameID());
        ChessGame game = gameData.game();
        game.makeMove(command.getMove());
        GameData updatedGame = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        gameDAO.updateGame(command.getGameID(), updatedGame);
        var loadGameMessage = new LoadGame(updatedGame);
        connections.sendGame(loadGameMessage, command.getGameID());

        var message = String.format("%s makes move %s", username, command.getMove().moveString());
        var notification = new Notification(message);
        connections.broadcast(null, notification, command.getGameID());
    }

    private void resign(String username, String opponent, int id, Session session) throws IOException {
        var message = String.format("%s resigned. %s wins!", username, opponent);
        var notification = new Notification(message);
        connections.broadcast(session, notification, id);
    }
}