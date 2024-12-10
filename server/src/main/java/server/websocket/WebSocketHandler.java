package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

import java.io.IOException;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final SQLUserDAO userDao = new SQLUserDAO();
    private final SQLGameDAO gameDao = new SQLGameDAO();
    private final SQLAuthDAO authDao = new SQLAuthDAO();


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println("Raw message received: " + message); // Log raw message for debugging

        try {
            // Parse the incoming message into a generic JSON object
            Gson gson = new Gson();
            UserGameCommand baseCommand = gson.fromJson(message, UserGameCommand.class);

            if (baseCommand.getCommandType() == null || baseCommand.getAuthToken() == null || baseCommand.getGameID() == null) {
                throw new IllegalArgumentException("Message is missing required fields (commandType, authToken, or gameID)");
            }

            // Handle the command type
            switch (baseCommand.getCommandType()) {
                case CONNECT -> connect(gson.fromJson(message, Connect.class), session);
                case MAKE_MOVE -> makeMove(gson.fromJson(message, MakeMove.class), session);
                case LEAVE -> leave(gson.fromJson(message, Leave.class), session);
                case RESIGN -> resign(gson.fromJson(message, Resign.class), session);
                default -> throw new IllegalArgumentException("Unsupported command type: " + baseCommand.getCommandType());
            }
        } catch (JsonSyntaxException e) {
            System.err.println("JSON syntax error: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Invalid JSON format: " + e.getMessage())));
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Invalid command structure: " + e.getMessage())));
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Error processing command: " + e.getMessage())));
        }
    }



    private void connect(Connect connect, Session session) throws IOException, ResponseException {
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
            String loadGameJson = new Gson().toJson(loadGame); // Serialize LoadGame to JSON
            connections.connections.get(connect.getAuthToken()).send(loadGameJson); // Send JSON response
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
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Failed to join the game: " + e.getMessage())));
        }
    }

    private void makeMove(MakeMove makeMove, Session session) throws IOException {
        try {
            AuthData authData = authDao.getAuth(makeMove.getAuthToken());
            GameData gameData = gameDao.getGame(makeMove.getGameID());

            if(authData == null) {
                throw new Exception("Invalid auth token");
            }

            gameData.game().makeMove(makeMove.move, Objects.equals(gameData.whiteUsername(), authData.username()) ?
                    ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK);
            gameDao.updateGame(new GameData(makeMove.getGameID(), gameData.whiteUsername(), gameData.blackUsername(),
                    gameData.gameName(), gameData.game()));
            //load game message to all clients in the game
            LoadGame loadGame = new LoadGame(gameData);
            connections.broadcast(" ", loadGame, makeMove.getGameID());
            //Server sends a Notification message to all other clients in that game about the move
            Notification notification = new Notification(authData.username() + " made the following move: " +
                    makeMove.move.toString());
            connections.broadcast(makeMove.getAuthToken(), notification, makeMove.getGameID());
            //If the move results in check or checkmate the server sends a Notification message to all clients.
            if (gameDao.getGame(makeMove.getGameID()).game().isInCheckmate(ChessGame.TeamColor.WHITE) ||  gameDao.
                    getGame(makeMove.getGameID()).game().isInCheck(ChessGame.TeamColor.WHITE) ){
                Notification notification2 = new Notification("Move resulted in check/checkmate for " + gameData.whiteUsername());
                connections.broadcast(" ", notification2, makeMove.getGameID());
            }
            if (gameDao.getGame(makeMove.getGameID()).game().isInCheckmate(ChessGame.TeamColor.BLACK) || gameDao.
                    getGame(makeMove.getGameID()).game().isInCheck(ChessGame.TeamColor.BLACK)){
                Notification notification2 = new Notification("Move resulted in check/checkmate for " + gameData.blackUsername());
                connections.broadcast(" ", notification2, makeMove.getGameID());
            }

        }
        catch(Exception e) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Unable to make the move: " + e.getMessage())));
        }
    }

    private void leave(Leave leave, Session session) throws IOException {
        try {
            AuthData authData = authDao.getAuth(leave.getAuthToken());
            GameData gameData = gameDao.getGame(leave.getGameID());
            if (authData.username().equals(gameData.whiteUsername())){
                gameDao.updateGame(new GameData(leave.getGameID(), null, gameData.blackUsername(),
                        gameData.gameName(), gameData.game()));
            }
            if (authData.username().equals(gameData.blackUsername())){
                gameDao.updateGame(new GameData(leave.getGameID(), gameData.whiteUsername(), null,
                        gameData.gameName(), gameData.game()));
            }
            connections.remove(leave.getAuthToken());
            //Server sends a Notification message to all other clients  in that game
            Notification notification = new Notification(authData.username() + " has left the game.");
            connections.broadcast(leave.getAuthToken(), notification, leave.getGameID());
        } catch (ResponseException | IOException e){
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Unable to leave")));
        }
    }

    private void resign(Resign resign, Session session) throws IOException {
        try {
            AuthData authData = authDao.getAuth(resign.getAuthToken());
            GameData gameData = gameDao.getGame(resign.getGameID());

            if (authData.username().equals(gameData.whiteUsername())){
                gameDao.updateGame(new GameData(resign.getGameID(), null, null,
                        gameData.gameName(), gameData.game()));
            }
            else if (authData.username().equals(gameData.blackUsername())){
                gameDao.updateGame(new GameData(resign.getGameID(), null, null,
                        gameData.gameName(), gameData.game()));
            } else {
                throw new Exception("Observer cannot resign");
            }
            //Server marks the game as over
            gameData.game().setEndGame();
            //Server sends a Notification message to all clients in that game
            Notification notification = new Notification(authData.username() + " has resigned.");
            connections.broadcast("", notification, resign.getGameID());
            connections.remove(resign.getAuthToken());
        }
        catch(Exception e) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Unable to resign: " + e.getMessage())));
        }
    }


}