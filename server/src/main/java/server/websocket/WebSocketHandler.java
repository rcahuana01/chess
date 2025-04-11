
package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
//import Notification;

import java.io.IOException;
import java.util.*;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager sessions = new ConnectionManager();
    private static final Map<Integer, ChessGame> validGames = new HashMap<>();

    static {
        try {
            SQLGameDAO gameDAO = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            SQLUserDAO userDAO = new SQLUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            SQLAuthDAO authDAO = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        String excSession = session.getRemoteAddress().toString();
        switch (command.getCommandType()) {
                case CONNECT -> connect(excSession, command, session);
                case LEAVE -> leave(excSession, command, session);
                case RESIGN -> resign(excSession, command, session);
                case MAKE_MOVE -> {
                    MakeMoveCommand makeCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                    makeMove(excSession, makeCommand, session);
                }

            }

    }

    private void connect(String excSession, UserGameCommand command, Session session) throws IOException {
        sessions.addSessionToGame(command.getGameID(), session);
        ChessGame game = new ChessGame();
        session.getRemote().sendString(new Gson().toJson(game));
        var message = String.format("%s has connected to chess", excSession);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        broadcast(session, command.getGameID(), message, notification);
    }

    private void makeMove(String excSession, MakeMoveCommand command, Session session) throws IOException {
        int gameId = command.getGameID();
        if (!validGames.containsKey(gameId)) {
            validGames.put(gameId, new ChessGame());
        }

        ChessGame game = validGames.get(gameId);
        ChessMove move = command.getMove();
        game.applyMove(move);
        String message = String.format("Player %s made a move: %s", excSession, move);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        broadcast(session, gameId, message, notification);
    }

    public void leave(String excSession, UserGameCommand command, Session session) throws DataAccessException {
        int gameId = command.getGameID();
        try {
            sessions.removeSessionFromGame(gameId, session);
            String message = String.format("Player %s left the game %d", excSession, gameId);
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            broadcast(session, gameId, message, notification);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private void resign(String excSession, UserGameCommand command, Session session) throws IOException {
        int gameId = command.getGameID();
        String authToken = command.getAuthToken();
        if (!"WHITE".equals(authToken) && !"BLACK".equals(authToken)) {
            return;
        }
        validGames.remove(gameId);

        String message = String.format("Player %s resigned from game %d", excSession, gameId);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        broadcast(session, gameId, message, notification);
    }


    public void broadcast(Session excSession, int gameId, String message, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Session>();
        for (Session c : sessions.getSessionsForGame(gameId)) {
            if (c.isOpen() && c!=excSession) {
                c.getRemote().sendString(new Gson().toJson(notification));
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (Session s : removeList) {
            sessions.removeSessionFromGame(gameId, s);
        }
    }

}
