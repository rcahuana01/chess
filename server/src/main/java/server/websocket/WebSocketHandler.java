
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
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand.CommandType action = new Gson().fromJson(message, UserGameCommand.CommandType.class);
        UserGameCommand.CommandType makeMove = new Gson().fromJson(message, MakeMoveCommand.CommandType.class);

        switch (action) {
            case CONNECT -> connect(action.name(), session);
            case MAKE_MOVE -> makeMove(makeMove.name());
            case LEAVE -> leave(action.name(), session);
            case RESIGN -> resign(action.name(), session);
        }
    }

    private void connect(String visitorName, Session session) throws IOException {
        connections.add(visitorName, session);
        var message = String.format("%s is in the shop", visitorName);
        var notification = new ServerMessage(Notification.Type.ARRIVAL, message);
        connections.broadcast(visitorName, notification);
    }

    private void makeMove(ChessMove move) throws IOException {
        connections.makeMove(move);
        var message = String.format("%s left the shop", visitorName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(visitorName, notification);
    }

    public void leave(String petName, String sound) throws DataAccessException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new Notification(Notification.Type.NOISE, message);
            connections.broadcast("", notification);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private void resign(String visitorName) throws IOException {
        connections.remove(visitorName);
        var message = String.format("%s left the shop", visitorName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(visitorName, notification);
    }
}
