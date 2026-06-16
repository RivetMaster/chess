package client;

import com.google.gson.Gson;
import exceptions.ResponseException;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameMove;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageHandler notificationHandler;

    public WebSocketFacade(String url, ServerMessageHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage msg = new Gson().fromJson(message, ServerMessage.class);
                    if(msg.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
                        Notification notification = new Gson().fromJson(message, Notification.class);
                        notificationHandler.notify(notification);
                    } else if(msg.getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)) {
                        ErrorMessage err = new Gson().fromJson(message, ErrorMessage.class);
                        notificationHandler.notifyError(err);
                    } else if(msg.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
                        LoadGame game = new Gson().fromJson(message, LoadGame.class);
                        notificationHandler.loadGameNotify(game);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, "Error: Could not connect to Server");
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void leaveGame(UserGameCommand command) throws ResponseException {
        send(command);
    }

    public void resignGame(UserGameCommand command) throws ResponseException {
        send(command);
    }

    public void connect(UserGameCommand command) throws ResponseException {
        send(command);
    }

    public void makeMove(UserGameMove command) throws ResponseException {
        send(command);
    }

    private <T extends UserGameCommand> void send(T command) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }
}
