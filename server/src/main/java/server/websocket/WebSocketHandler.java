
package server.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
//import Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager sessions = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getGameID(), command.getAuthToken(), session);
                case LEAVE -> leave(command.getGameID(), session);
                case RESIGN -> resign(action.name(), session);
                case MAKE_MOVE -> makeMove(makeMove);

            }

    }

    private void connect(String excSession, UserGameCommand command, Session session) throws IOException {
        sessions.addSessionToGame(command.getGameID(), session);
        var message = String.format("%s is connected to chess", excSession);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        broadcast(session, command.getGameID(), message, notification);
    }

    private void makeMove(String excSession, MakeMoveCommand command, Session session) throws IOException {
        sessions.addSessionToGame(command.getGameID(), session);
        var message = String.format("%s made a move %s", excSession, command.getMove());
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        broadcast(session, command.getGameID(), message, notification);
    }

    public void leave(String excSession, MakeMoveCommand command, Session session) throws DataAccessException {
        try {
            sessions.removeSessionFromGame(command.getGameID());

            var message = String.format("%s says %s", petName, sound);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            broadcast(session, gameId, message, notification);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private void resign(String excSession, int gameId, Session session) throws IOException {
        sessions.removeSessionFromGame();
        var message = String.format("%s left the shop", excSession);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        broadcast(session, gameId, message, notification);
    }


    public void broadcast(Session excSession, int gameId, String message, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : sessions.getConnectionsForGame(gameId)) {
            if (c.session.isOpen() && c.session!=excSession) {
                c.sendMessage(notification.toString(), c.session);
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            sessions.getConnectionsForGame(gameId).remove(c);
        }
    }

}
