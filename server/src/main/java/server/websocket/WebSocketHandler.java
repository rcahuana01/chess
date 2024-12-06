package server.websocket;

import chess.ChessGame;
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
import websocket.messages.ServerMessage;

import javax.management.NotificationFilter;
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
        System.out.println("WebSocket message received: " + message);

        // Deserialize the message into a UserGameCommand object.
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);

        // Perform an action based on the command type.
        switch (action.getCommandType()) {
            case CONNECT -> connect(new Gson().fromJson(message, Connect.class), session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMove.class), session);
            case LEAVE -> leave(new Gson().fromJson(message, Leave.class), session);
            case RESIGN -> resign(new Gson().fromJson(message, Resign.class), session);
            default -> session.getRemote().sendString(new Gson().toJson(new Error("Unknown command type")));

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
            System.out.println("Received connect request: " + new Gson().toJson(connect));

            // Retrieve authentication data for the provided token.
            AuthData authData = authDao.getAuth(connect.getAuthToken());
            System.out.println("Authentication data retrieved: " + (authData != null ? authData.toString() : "null"));

            // Retrieve game data for the provided game ID.
            GameData gameData = gameDao.getGame(connect.getGameID());
            System.out.println("Game data retrieved: " + (gameData != null ? gameData.toString() : "null"));
            if (gameData == null) {
                String errorMessage = new Gson().toJson(new Error("Invalid game ID: " + connect.getGameID()));
                System.err.println("Sending error message to client: " + errorMessage);
                session.getRemote().sendString(errorMessage);
                return; // Stop further processing
            }



            // Validate the authentication token.
            if (authData == null) {
                System.err.println("Invalid auth token: " + connect.getAuthToken());
                throw new Exception("Invalid auth token");
            }

            // Add the connection to the ConnectionManager.
            connections.add(connect.getAuthToken(), session, connect.getGameID());
            System.out.println("Connection added for token: " + connect.getAuthToken());

            // Create a LoadGame message and send it to the connected client.
            LoadGame loadGame = new LoadGame(gameData);
            String jsonMessage = new Gson().toJson(loadGame);
            System.out.println("Sending LoadGame message to client: " + jsonMessage);
            connections.connections.get(connect.getAuthToken()).send(jsonMessage);

            if (connect.observer) {
                // Send a notification for an observer joining the game.
                Notification notification = new Notification(authData.username() + " joined the game as observer");
                System.out.println("Broadcasting observer join notification: " + notification.getMessage());
                connections.broadcast(connect.getAuthToken(), notification, connect.getGameID());
            } else {
                // Determine the player's color and send a notification.
                String playerColor = Objects.equals(gameData.whiteUsername(), authData.username()) ? "white" : "black";
                Notification notification = new Notification(authData.username() + " joined the game as " + playerColor);
                System.out.println("Broadcasting player join notification: " + notification.getMessage());
                connections.broadcast(connect.getAuthToken(), notification, connect.getGameID());
            }

        } catch (Exception e) {
            System.err.println("Exception in connect: " + e.getMessage());
            String errorResponse = new Gson().toJson(new Error("Failed to join the game: " + e.getMessage()));
            try {
                session.getRemote().sendString(errorResponse);
            } catch (IOException ioException) {
                System.err.println("Failed to send error response: " + ioException.getMessage());
            }

        }

    }


    private void makeMove(MakeMove makeMove, Session session) throws  IOException {
        try {
            AuthData authData = authDao.getAuth(makeMove.getAuthToken());
            GameData gameData = gameDao.getGame(makeMove.getGameID());
            if (authData==null){
                throw new Exception("Invalid auth token");
            }
            gameData.game().makeMove(makeMove.move);
            LoadGame loadGame = new LoadGame(gameData);
            connections.broadcast(" ", loadGame, makeMove.getGameID());
            // Server sends a Notification message to all other clients
            Notification notification = new Notification(authData.username());
            connections.broadcast(makeMove.getAuthToken(), notification, makeMove.getGameID());
            if (gameDao.getGame(makeMove.getGameID()).game().isInCheckmate(ChessGame.TeamColor.WHITE) || gameDao.getGame(makeMove.getGameID()).game().isInCheck(ChessGame.TeamColor.WHITE)){
                Notification notification2 = new Notification("Move will result in check/checkmate for" + gameData.whiteUsername());
                connections.broadcast(" ",notification2, makeMove.getGameID());
            }
            if (gameDao.getGame(makeMove.getGameID()).game().isInCheckmate(ChessGame.TeamColor.BLACK) || gameDao.getGame(makeMove.getGameID()).game().isInCheck(ChessGame.TeamColor.BLACK)){
                Notification notification2 = new Notification("Move will result in check/checkmate for" + gameData.blackUsername());
                connections.broadcast(" ",notification2, makeMove.getGameID());
            }
        }
        catch (Exception e) {
            session.getRemote().sendString(new Gson().toJson(new Error("Unable to make move: " + e.getMessage())));
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
            connections.remove(leave.getAuthToken());

            Notification notification = new Notification(authData.username() + "left the game.");
            connections.broadcast(leave.getAuthToken(), notification, leave.getGameID());
        } catch (Exception e) {
            session.getRemote().sendString(new Gson().toJson(new Error("Unable to leave. ")));
        }
    }

    private void resign(Resign resign, Session session) throws IOException {
        try {
            AuthData authData = authDao.getAuth(resign.getAuthToken());
            GameData gameData = gameDao.getGame(resign.getGameID());
            if (authData.username().equals(gameData.whiteUsername())){
                gameDao.updateGame(new GameData(resign.getGameID(),null,null, gameData.gameName(), gameData.game()));
            } else if (authData.username().equals(gameData.blackUsername())){
                gameDao.updateGame(new GameData(resign.getGameID(),null,null, gameData.gameName(),gameData.game()));
            } else {
                throw new Exception("Observer cannot resign. ");
            }
            gameData.game().setEndGame();
            Notification notification = new Notification(authData.username() + "has resigned.");
            connections.broadcast("",notification, resign.getGameID());
            connections.remove(resign.getAuthToken());
        }
        catch (Exception e){
            session.getRemote().sendString(new Gson().toJson(new Error("Unable to resign: " + e.getMessage())));
        }
    }
}
