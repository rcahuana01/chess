package server.websocket;

import com.google.gson.Gson; // Used for JSON serialization and deserialization.
import dataaccess.*; // Data access objects for interacting with the database.
import model.AuthData; // Represents authentication data for users.
import model.GameData; // Represents game data.
import org.eclipse.jetty.websocket.api.Session; // Represents a WebSocket session.
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage; // Handles WebSocket message events.
import org.eclipse.jetty.websocket.api.annotations.WebSocket; // Marks this class as a WebSocket handler.
import websocket.commands.*; // Commands sent via WebSocket.
import websocket.messages.LoadGame; // Message to load game data.
import websocket.messages.Notification; // Notification message.

import java.io.IOException;
import java.util.Objects;

/**
 * Handles WebSocket interactions, including managing connections,
 * processing incoming messages, and sending responses.
 */
@WebSocket
public class WebSocketHandler {

    // Manages active WebSocket connections.
    private final ConnectionManager connections = new ConnectionManager();

    // Data access objects for interacting with the database.
    private final SQLUserDAO userDao = new SQLUserDAO(); // Handles user data.
    private final SQLGameDAO gameDao = new SQLGameDAO(); // Handles game data.
    private final SQLAuthDAO authDao = new SQLAuthDAO(); // Handles authentication data.

    /**
     * Handles incoming WebSocket messages from clients.
     *
     * @param session The WebSocket session associated with the client.
     * @param message The incoming message as a String.
     * @throws ResponseException If an error occurs while processing the response.
     * @throws Exception         If a general error occurs.
     */
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws ResponseException, Exception {
        // Deserialize the message into a UserGameCommand object.
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);

        // Perform an action based on the command type.
        switch (action.getCommandType()) {
            case CONNECT -> connect(new Gson().fromJson(message, Connect.class), session);
        }
    }

    /**
     * Handles the connection process for a client attempting to join a game.
     *
     * @param connect The connection details sent by the client.
     * @param session The WebSocket session associated with the client.
     * @throws Exception         If an error occurs during the connection process.
     * @throws ResponseException If a response-specific error occurs.
     */
    private void connect(Connect connect, Session session) throws Exception, ResponseException {
        try {
            // Retrieve authentication data for the provided token.
            AuthData authData = authDao.getAuth(connect.getAuthToken());

            // Retrieve game data for the provided game ID.
            GameData gameData = gameDao.getGame(connect.getGameID());

            // Validate the authentication token.
            if (authData == null) {
                throw new Exception("Invalid auth token");
            }

            // Validate the game ID.
            if (gameData == null) {
                throw new Exception("Invalid game ID");
            }

            // Add the connection to the ConnectionManager.
            connections.add(connect.getAuthToken(), session, connect.getGameID());

            // Create a LoadGame message and send it to the connected client.
            LoadGame loadGame = new LoadGame(gameData);
            connections.connections.get(connect.getAuthToken()).send(loadGame.toString());            // Handle notifications based on the observer status.
            if (connect.observer) {
                // Send a notification for an observer joining the game.
                Notification notification = new Notification(authData.username() + " joined the game as observer");
                connections.broadcast(connect.getAuthToken(), notification, connect.getGameID());
            } else {
                // Determine the player's color and send a notification.
                String playerColor = Objects.equals(gameData.whiteUsername(), authData.username()) ? "white" : "black";
                Notification notification = new Notification(authData.username() + " joined the game as " + playerColor);
                connections.broadcast(connect.getAuthToken(), notification, connect.getGameID());
            }

        } catch (Exception e) {
            // Send an error message back to the client if an exception occurs.
            session.getRemote().sendString(new Gson().toJson(new Error("Failed to join the game: " + e.getMessage())));
        }
    }
    private void leave(Leave leave, Session session) throws IOException {
        try {
            AuthData authData = authDao.getAuth(leave.getAuthToken());
            GameData gameData = gameDao.getGame(leave.getGameID());
            if (authData.username().equals(gameData.whiteUsername())){
                gameDao.updateGame(new GameData(leave.getGameID(),null, gameData.blackUsername(), gameData.gameName(),gameData.game()));
            }
            if (authData.username().equals(gameData.blackUsername())){
                gameDao.updateGame(new GameData(leave.getGameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game()));
            }
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }
}
