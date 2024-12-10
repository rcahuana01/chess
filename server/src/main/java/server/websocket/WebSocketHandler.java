package server.websocket;

import chess.ChessGame;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson; // Used for JSON serialization and deserialization.
import dataaccess.*; // Data access objects for interacting with the database.
import model.AuthData; // Represents authentication data for users.
import model.GameData; // Represents game data.
import org.eclipse.jetty.websocket.api.Session; // Represents a WebSocket session.
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage; // Handles WebSocket message events.
import org.eclipse.jetty.websocket.api.annotations.WebSocket; // Marks this class as a WebSocket handler.
import websocket.commands.*; // Commands sent via WebSocket.
import websocket.messages.ErrorMessage;
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
            AuthData authData = authDao.getAuth(connect.getAuthToken());
            GameData gameData = gameDao.getGame(connect.getGameID());

            if(authData == null) {
                throw new Exception("Invalid auth token");
            }
            if(gameData == null) {
                throw new Exception("Invalid game ID");
            }

            connections.add(connect.getAuthToken(), session, connect.getGameID());
            //Server sends a LOAD_GAME message back to the root client
            LoadGame loadGame = new LoadGame(gameData);
            session.getRemote().sendString(new Gson().toJson(loadGame));
            //Server sends a Notification message to all other clients in that game informing them the root client connected
            if (connect.observer){
                Notification notification = new Notification(authData.username() + " joined the game as observer");
                connections.broadcast(connect.getAuthToken(), notification, connect.getGameID());
            }else {
                String playerColor = Objects.equals(gameData.whiteUsername(), authData.username()) ? "white" : "black";
                Notification notification = new Notification(authData.username() + " joined the game as " + playerColor);
                connections.broadcast(connect.getAuthToken(), notification, connect.getGameID());
            }
        }
        catch (Exception e) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Failed to join the game: ")));
        }
    }


    private void makeMove(MakeMove makeMove, Session session) throws IOException {
        try {
            // Validate auth token and fetch game data
            AuthData authData = authDao.getAuth(makeMove.getAuthToken());
            if (authData == null) {
                throw new Exception("Invalid auth token");
            }

            GameData gameData = gameDao.getGame(makeMove.getGameID());
            if (gameData == null) {
                throw new Exception("Invalid game ID");
            }

            // Determine the player's team color
            ChessGame.TeamColor playerColor = authData.username().equals(gameData.whiteUsername())
                    ? ChessGame.TeamColor.WHITE
                    : authData.username().equals(gameData.blackUsername())
                    ? ChessGame.TeamColor.BLACK
                    : null;

            if (playerColor == null) {
                throw new Exception("Unauthorized user. You are not a player in this game.");
            }

            // Validate the piece being moved
            ChessPiece movingPiece = gameData.game().getBoard().getPiece(makeMove.move.getStartPosition());
            if (movingPiece == null) {
                throw new InvalidMoveException("No piece at the start position.");
            }

            // Check if the player is attempting to move an opponent's piece
            if (movingPiece.getTeamColor() != playerColor) {
                throw new InvalidMoveException("You cannot move your opponent's piece.");
            }

            // Validate the player's turn
            if (playerColor != gameData.game().getTeamTurn()) {
                throw new InvalidMoveException("It's not your turn!");
            }

            // Execute the move
            gameData.game().makeMove(makeMove.move);

            // Persist the updated game state
            gameDao.updateGame(new GameData(makeMove.getGameID(), gameData.whiteUsername(), gameData.blackUsername(),
                    gameData.gameName(), gameData.game()));

            // Check for in-check or checkmate states
            ChessGame.TeamColor opponentColor = (playerColor == ChessGame.TeamColor.WHITE)
                    ? ChessGame.TeamColor.BLACK
                    : ChessGame.TeamColor.WHITE;

            // Check if the opponent is in checkmate
            if (gameData.game().isInCheckmate(opponentColor)) {
                Notification checkmateNotification = new Notification("Checkmate! " +
                        (playerColor == ChessGame.TeamColor.WHITE ? "White" : "Black") + " wins!");
                connections.broadcast("", checkmateNotification, makeMove.getGameID());
            } else if (gameData.game().isInCheck(opponentColor)) {
                // Check if the opponent is in check
                Notification checkNotification = new Notification("Check! " +
                        (playerColor == ChessGame.TeamColor.WHITE ? "Black" : "White") + " is in check.");
                connections.broadcast("", checkNotification, makeMove.getGameID());
            }


            // Send the updated game state (LOAD_GAME) to all players
            LoadGame loadGame = new LoadGame(gameData);
            connections.broadcast("", loadGame, makeMove.getGameID());

            // Send a notification to other players (excluding the player who made the move)
            Notification notification = new Notification(authData.username() + " made a move from " + "(" +
                    makeMove.move.getStartPosition().getRow() + "," + makeMove.move.getStartPosition().getColumn() + ")" +
                    " to " + "(" + makeMove.move.getEndPosition().getRow() + "," + makeMove.move.getEndPosition().getColumn() + ")");

            connections.broadcast(makeMove.getAuthToken(), notification, makeMove.getGameID());

        } catch (InvalidMoveException e) {
            // Send an error notification for invalid moves
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Invalid move. " )));
        } catch (Exception e) {
            // Send a generic error message for other exceptions
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Unable to make move. ")));
        }
    }



    private void leave(Leave leave, Session session) throws IOException {
        try {
            AuthData authData = authDao.getAuth(leave.getAuthToken());
            GameData gameData = gameDao.getGame(leave.getGameID());
            if (authData.username().equals(gameData.whiteUsername())){
                gameDao.updateGame(new GameData(leave.getGameID(),null, gameData.blackUsername(),
                        gameData.gameName(),gameData.game()));
            }
            if (authData.username().equals(gameData.blackUsername())){
                gameDao.updateGame(new GameData(leave.getGameID(), gameData.whiteUsername(), null,
                        gameData.gameName(), gameData.game()));
            }
            connections.remove(leave.getAuthToken());

            Notification notification = new Notification(authData.username() + "left the game.");
            connections.broadcast(leave.getAuthToken(), notification, leave.getGameID());
        } catch (Exception e) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Unable to leave. ")));
        }
    }

    private void resign(Resign resign, Session session) throws IOException {
        try {
            AuthData authData = authDao.getAuth(resign.getAuthToken());
            GameData gameData = gameDao.getGame(resign.getGameID());
            if (authData.username().equals(gameData.whiteUsername())){
                gameDao.updateGame(new GameData(resign.getGameID(),null,null, gameData.gameName(),
                        gameData.game()));
            } else if (authData.username().equals(gameData.blackUsername())){
                gameDao.updateGame(new GameData(resign.getGameID(),null,null, gameData.gameName(),
                        gameData.game()));
            } else {
                throw new Exception("Observer cannot resign. ");
            }
            gameData.game().setEndGame();
            Notification notification = new Notification(authData.username() + "has resigned.");
            connections.broadcast("",notification, resign.getGameID());
            connections.remove(resign.getAuthToken());
        }
        catch (Exception e){
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Unable to resign: " + e.getMessage())));
        }
    }
}
