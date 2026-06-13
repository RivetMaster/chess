package WebSocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.GameDAO;
import exceptions.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import service.AuthService;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;
import websocket.commands.UserGameMove;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ServerMessageType;

import java.io.IOException;

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO;

    public WebSocketHandler(GameDAO gameDAO){
        super();
        this.gameDAO = gameDAO;
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
                case MAKE_MOVE -> makeMove(new Gson().fromJson(ctx.message(), UserGameMove.class), ctx.session);
                case LEAVE -> leave(action.);
                case RESIGN -> resign();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(int gameID, String username, Session session) throws IOException {
        connections.add(session, gameID);
        var message = String.format("%s joined the game", username); //no bc called when join and when observe
        var notification = new ServerMessage(ServerMessageType.NOTIFICATION, message);
        connections.broadcast(session, notification, gameID);
    }

    private void leave(String username, int gameID, Session session) throws IOException {
        var message = String.format("%s left the game", username);
        var notification = new ServerMessage(ServerMessageType.NOTIFICATION, message);
        connections.broadcast(session, notification, gameID);
        connections.remove(session, gameID);
    }

    public void makeMove(UserGameMove command, String username, Session session) throws ResponseException {
        try {
            //check that can make move, turn, both players in game
            GameData gameData = gameDAO.getGame(command.getGameID());
            ChessGame game = gameData.game();
            game.makeMove(command.getMove());
            gameDAO.updateGame(command.getGameID(), new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
            var message = String.format("%s makes move %s", username, command.getMove().moveString());
            var notification = new ServerMessage(ServerMessageType.NOTIFICATION, message);
            connections.broadcast(null, notification, command.getGameID());
        } catch(InvalidMoveException e) {
            throw new ResponseException(ResponseException.Code.ClientError, e.getMessage());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private void resign(String visitorName, Session session) throws IOException {
        var message = String.format("%s left the shop", visitorName);
        var notification = new ServerMessage(ServerMessageType.NOTIFICATION, message);
        connections.broadcast(session, notification);
    }
}