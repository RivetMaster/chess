package client;

import websocket.messages.*;

public interface ServerMessageHandler {
    void notify(Notification notification);
    void notifyError(ErrorMessage error);
    void loadGameNotify(LoadGame game);
}