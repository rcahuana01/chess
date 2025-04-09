package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final Map<Integer, Set<Connection>> connections = new HashMap<>();

    public void addSessionToGame(int gameId, Session session) {
        Connection c = new Connection(gameId, session);
        if (connections.get(gameId)==null){
            Set<Connection> set = new HashSet<>();
            set.add(c);
            connections.put(gameId, set);
        } else {
            connections.get(gameId).add(c);
        }
    }

    public void removeSessionFromGame(Connection c) {
        connections.get(c.gameId).remove(c);
    }

    public Set<Connection> getConnectionsForGame(int gameId) {
        return connections.get(gameId);
    }

}