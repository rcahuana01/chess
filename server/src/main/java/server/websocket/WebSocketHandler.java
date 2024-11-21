package server.websocket;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.LoadGame;

import javax.management.Notification;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final SQLUserDAO userDao = new SQLUserDAO();
    private final SQLGameDAO gameDao = new SQLGameDAO();
    private final SQLAuthDAO authDao = new SQLAuthDAO();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws ResponseException, Exception {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);

        switch (action.getCommandType()) {
            case CONNECT -> connect(new Gson().fromJson(message, Connect.class), session);

        }

    }

    private void connect(Connect connect, Session session) throws Exception, ResponseException {
        try {
            AuthData authData = authDao.getAuth(connect.getAuthToken());
            GameData gameData = gameDao.getGame(connect.getGameID());

            if (authData == null) {
                throw new Exception("Invalid auth token");
            }
            if (gameData == null) {
                throw new Exception("Invalid game ID");
            }

            connections.add(connect.getAuthToken(), session, connect.getGameID());


            connections.add(connect.getAuthToken(), session, connect.getGameID());

            LoadGame loadGame = new LoadGame(gameData);
            connections.connections.get(connect.getAuthToken()).send(loadGame.toString());
            if (connect.observer){
                Notification notification = new Notification(authData.username() + "joined the game as observer");
                connections.broadcast(connect.getAuthToken(), notification, connect.getGameID());
            } else {
                String playerColor = Objects.equals(gameData.whiteUsername(), authData.username()) ? "white" : "black";
                Notification notification = new Notification(authData.username() + "joined the game as " + playerColor);
                connections.broadcast(connect.getAuthToken(), notification, connect.getGameID());
            }


        } catch (Exception e) {
            session.getRemote().sendString(new Gson().toJson(new Error("Failed to join the game: " + e.getMessage())));
        }
    }


}
