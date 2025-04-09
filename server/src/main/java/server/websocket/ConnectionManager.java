package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final Map<Integer, Set<Session>> sessionMap = new HashMap<>();

    public void addSessionToGame(int gameId, Session session) {
        if (sessionMap.get(gameId)==null){
            Set<Session> set = new HashSet<>();
            set.add(session);
            sessionMap.put(gameId, set);
        } else {
            sessionMap.get(gameId).add(session);
        }
    }

    public void removeSessionFromGame(int gameId, Session session) {
        sessionMap.remove(gameId);

    }

    public Set<Session> getSessionForGame(int gameId) {
        return sessionMap.get(gameId);
    }

//    public void broadcast(String excludeVisitorName, ServerMessage notification) throws IOException {
//        var removeList = new ArrayList<Connection>();
//        for (var c : connections.values()) {
//            if (c.session.isOpen()) {
//                if (!c.visitorName.equals(excludeVisitorName)) {
//                    c.send(notification.toString());
//                }
//            } else {
//                removeList.add(c);
//            }
//        }
//
//        // Clean up any connections that were left open.
//        for (var c : removeList) {
//            connections.remove(c.visitorName);
//        }
//    }
}