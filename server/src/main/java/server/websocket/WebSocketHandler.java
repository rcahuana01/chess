package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.commands.UserGameCommand;
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
    public void onMessage(Session session, String message) throws ResponseException, IOException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);

        switch (action.getCommandType()) {
            case CONNECT -> connect(new Gson().fromJson(message, Connect.class), session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMove.class), session);
            case LEAVE -> leave(new Gson().fromJson(message, Leave.class), session);
            case RESIGN -> resign(new Gson().fromJson(message, Resign.class),session);
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
            LoadGame loadGame = new LoadGame(gameData);
            connections.connections.get(connect.getAuthToken()).send(loadGame.toString());
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
            session.getRemote().sendString(new Gson().toJson(new Error("Failed to join the game: " + e.getMessage())));
        }
    }

    private void makeMove(MakeMove makeMove, Session session) throws IOException {
        try {
            AuthData authData = authDao.getAuth(makeMove.getAuthToken());
            GameData gameData = gameDao.getGame(makeMove.getGameID());

            if(authData == null) {
                throw new Exception("Invalid auth token");
            }

            gameData.game().makeMove(makeMove.move, Objects.equals(gameData.whiteUsername(), authData.username()) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK);
            gameDao.updateGame(new GameData(makeMove.getGameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.game()));
            LoadGame loadGame = new LoadGame(gameData);
            connections.broadcast(" ", loadGame, makeMove.getGameID());
            Notification notification = new Notification(authData.username() + " made the following move: " + makeMove.move.toString());
            connections.broadcast(makeMove.getAuthToken(), notification, makeMove.getGameID());
            if (gameDao.getGame(makeMove.getGameID()).game().isInCheckmate(ChessGame.TeamColor.WHITE) ||  gameDao.getGame(makeMove.getGameID()).game().isInCheck(ChessGame.TeamColor.WHITE) ){
                Notification notification2 = new Notification("Move resulted in check/checkmate for " + gameData.whiteUsername());
                connections.broadcast(" ", notification2, makeMove.getGameID());
            }
            if (gameDao.getGame(makeMove.getGameID()).game().isInCheckmate(ChessGame.TeamColor.BLACK) || gameDao.getGame(makeMove.getGameID()).game().isInCheck(ChessGame.TeamColor.BLACK)){
                Notification notification2 = new Notification("Move resulted in check/checkmate for " + gameData.blackUsername());
                connections.broadcast(" ", notification2, makeMove.getGameID());
            }

        }
        catch(Exception e) {
            session.getRemote().sendString(new Gson().toJson(new Error("Unable to make the move: " + e.getMessage())));
        }
    }

    private void leave(Leave leave, Session session) throws IOException {
        try {
            AuthData authData = authDao.getAuth(leave.getAuthToken());
            GameData gameData = gameDao.getGame(leave.getGameID());
            if (authData.username().equals(gameData.whiteUsername())){
                gameDao.updateGame(new GameData(leave.getGameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game()));
            }
            if (authData.username().equals(gameData.blackUsername())){
                gameDao.updateGame(new GameData(leave.getGameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game()));
            }
            connections.remove(leave.getAuthToken());
            Notification notification = new Notification(authData.username() + " has left the game.");
            connections.broadcast(leave.getAuthToken(), notification, leave.getGameID());
        } catch (Exception e){
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Unable to leave")));
        }
    }

    private void resign(Resign resign, Session session) throws IOException {
        try {
            AuthData authData = authDao.getAuth(resign.getAuthToken());
            GameData gameData = gameDao.getGame(resign.getGameID());

            if (authData.username().equals(gameData.whiteUsername())){
                gameDao.updateGame(new GameData(resign.getGameID(), null, null, gameData.gameName(), gameData.game()));
            }
            else if (authData.username().equals(gameData.blackUsername())){
                gameDao.updateGame(new GameData(resign.getGameID(), null, null, gameData.gameName(), gameData.game()));
            } else {
                throw new Exception("Observer cannot resign");
            }
            gameData.game().setEndGame();
            Notification notification = new Notification(authData.username() + " has resigned.");
            connections.broadcast("", notification, resign.getGameID());
            connections.remove(resign.getAuthToken());
        }
        catch(Exception e) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Unable to resign: " + e.getMessage())));
        }
    }


}