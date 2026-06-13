package WebSocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(Session session, int id) {
        connections.putIfAbsent(id, new HashSet<Session>());
        connections.get(id).add(session);
    }

    public void remove(Session session, int id) {
        connections.get(id).remove(session);
    }

    public void broadcast(Session excludeSession, Notification notification, int id) throws IOException {
        String msg = new Gson().toJson(notification);
        for (Session c : connections.get(id)) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }

    public void sendGame(LoadGame gameMsg, int id) throws IOException {
        String gameJson = new Gson().toJson(gameMsg);
        for (Session c : connections.get(id)) {
            if (c.isOpen()) {
                c.getRemote().sendString(gameJson);
            }
        }
    }
}