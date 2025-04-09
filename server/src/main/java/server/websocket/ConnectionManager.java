package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
        connections.get(gameId).remove(session);
    }

    public Set<Session> getSessionsForGame(int gameId) {
        return connections.get(gameId);
    }

}