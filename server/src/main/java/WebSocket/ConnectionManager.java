package WebSocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

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

    public void broadcast(Session excludeSession, ServerMessage notification, int id) throws IOException {
        String msg = notification.toString();
        for (Session c : connections.get(id)) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}