package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConnectionManager {
    public final Map<Integer, Set<Session>> connections = new HashMap<>();

    public void addSessionToGame(int gameId, Session session) {
        if (connections.get(gameId)==null){
            Set<Session> set = new HashSet<>();
            set.add(session);
            connections.put(gameId, set);
        } else {
            connections.get(gameId).add(session);
        }
    }

    public void removeSessionFromGame(int gameId, Session session) {
        if (connections.containsKey(gameId)){
            connections.get(gameId).remove(session);
            if (connections.get(gameId).isEmpty()){
                connections.remove(gameId);
            }
        }
    }

    public Set<Session> getSessionsForGame(int gameId) {
        return connections.getOrDefault(gameId, new HashSet<>());
    }

}