package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import dataaccess.SQLUserDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager sessions = new ConnectionManager();
    private final Map<Integer, GameState> gameStates = new HashMap<>();
    private final SQLGameDAO gameDao;
    private final SQLAuthDAO authDao;

    {
        try {
            gameDao = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    {
        try {
            authDao = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        try {
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, session);
                case LEAVE   -> leave(command, session);
                case RESIGN  -> resign(command, session);
                case MAKE_MOVE -> {
                    MakeMoveCommand makeMoveCmd = new Gson().fromJson(message, MakeMoveCommand.class);
                    makeMove(makeMoveCmd, session);
                }
            }
        } catch (DataAccessException ex) {
            sendError(session, "Error: " + ex.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException, DataAccessException {
        AuthData authData = authDao.getAuthToken(command.getAuthToken());
        GameData gameData = gameDao.getGame(command.getGameID());
        if (authData == null) {
            throw new DataAccessException("Invalid auth token");
        }
        if (gameData == null) {
            throw new DataAccessException("Invalid gameId");
        }

        String username = authData.username();
        if (!gameStates.containsKey(command.getGameID())) {
            GameState gs = new GameState();
            if (username.equals(gameData.whiteUsername())) {
                gs.whitePlayer = username;
            } else if (username.equals(gameData.blackUsername())) {
                gs.blackPlayer = username;
            } else {
                gs.observers.add(username);
            }
            gameStates.put(command.getGameID(), gs);
        }

        GameState gameState = gameStates.get(command.getGameID());
        if (gameState.isOver) {
            throw new DataAccessException("Game is over, cannot connect");
        }
        String note = "";
        if (!username.equals(gameState.whitePlayer) &&
                !username.equals(gameState.blackPlayer) &&
                !gameState.observers.contains(username)) {
            if (gameState.whitePlayer == null && username.equals(gameData.whiteUsername())) {
                gameState.whitePlayer = username;
                note = username + " has joined as " + "WHITE";

            } else if (gameState.blackPlayer == null && username.equals(gameData.blackUsername())) {
                gameState.blackPlayer = username;
                note = username + " has joined as " + "BLACK";

            } else {
                gameState.observers.add(username);
                note = username + " has joined as observer.";

            }
        }

        sessions.addSessionToGame(command.getGameID(), session);
        ServerMessage loadGameMsg = new ServerMessage(gameState.game);
        session.getRemote().sendString(new Gson().toJson(loadGameMsg));
        broadcastNotification(command.getGameID(), note, session);
    }

    private void leave(UserGameCommand command, Session session) throws DataAccessException, IOException, SQLException {
        if (!gameStates.containsKey(command.getGameID())) {
            throw new DataAccessException("Invalid game ID for leave");
        }

        GameState gameState = gameStates.get(command.getGameID());
        GameData gameData = gameDao.getGame(command.getGameID());
        AuthData authData = authDao.getAuthToken(command.getAuthToken());
        String username = authData.username();

        if (username.equals(gameData.whiteUsername())) {
            gameDao.updateGameList(new GameData(command.getGameID(),null,gameData.blackUsername(), gameData.gameName(),
                    gameData.game()));
        }
        if (username.equals(gameData.blackUsername())) {
            gameDao.updateGameList(new GameData(command.getGameID(), gameData.whiteUsername(), null, gameData.gameName(),
                    gameData.game()));
        }

        if (username.equals(gameState.whitePlayer)) {
            gameState.whitePlayer = null;
        } else if (username.equals(gameState.blackPlayer)) {
            gameState.blackPlayer = null;
        } else {
            gameState.observers.remove(username);
        }

        sessions.removeSessionFromGame(command.getGameID(), session);
        String note = username + " left the game ";
        broadcastNotification(command.getGameID(), note, session);
    }



    private void resign(UserGameCommand command, Session session) throws DataAccessException, IOException {
        if (!gameStates.containsKey(command.getGameID())) {
            throw new DataAccessException("Invalid game ID for resign");
        }
        GameState gameState = gameStates.get(command.getGameID());
        AuthData authData = authDao.getAuthToken(command.getAuthToken());
        String username = authData.username();

        if (!username.equals(gameState.whitePlayer) && !username.equals(gameState.blackPlayer)) {
            throw new DataAccessException("Observers cannot resign");
        }
        if (gameState.isOver) {
            ServerMessage msg = new ServerMessage("Game is already over");
            session.getRemote().sendString(new Gson().toJson(msg));
            return;
        }
        gameState.isOver = true;
        if (username.equals(gameState.whitePlayer)) {
            gameState.whitePlayer = null;
        } else {
            gameState.blackPlayer = null;
        }
        String note = username + " resigned from game ";
        broadcastNotification(command.getGameID(), note, null);
        sessions.removeSessionFromGame(command.getGameID(), session);
    }

    private void makeMove(MakeMoveCommand command, Session session) throws DataAccessException, IOException {
        AuthData authData = authDao.getAuthToken(command.getAuthToken());
        if (authData == null) {
            throw new DataAccessException("Invalid auth token");
        }
        String username = authData.username();
        if (!gameStates.containsKey(command.getGameID())) {
            throw new DataAccessException("Invalid game ID for move");
        }
        GameState gs = gameStates.get(command.getGameID());
        if (gs.isOver) {
            throw new DataAccessException("Game is already over, cannot move");
        }

        boolean isWhite = username.equals(gs.whitePlayer);
        boolean isBlack = username.equals(gs.blackPlayer);
        if (!isWhite && !isBlack) {
            throw new DataAccessException("Observer or unknown user cannot move");
        }
        ChessGame.TeamColor currentTurn = gs.game.getTeamTurn();
        if ((currentTurn == ChessGame.TeamColor.WHITE && !isWhite) ||
                (currentTurn == ChessGame.TeamColor.BLACK && !isBlack)) {
            throw new DataAccessException("It is not your turn");
        }
        ChessMove move = command.getMove();
        try {
            gs.game.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new DataAccessException("Invalid move");
        }
        ServerMessage loadGameMsg = new ServerMessage(gs.game);
        broadcastMessage(command.getGameID(), loadGameMsg);

        String note = username + " made a move: " + move.getStartPosition().toString() + "," + move.getEndPosition().toString();
        broadcastNotification(command.getGameID(), note, session);
        ChessGame.TeamColor next = gs.game.getTeamTurn();
        if (gs.game.isInCheck(next)) {
            if (gs.game.isInCheckmate(next)) {
                broadcastNotification(command.getGameID(),
                        (next == ChessGame.TeamColor.WHITE ? gs.whitePlayer : gs.blackPlayer) + " is in checkmate", null);
                gs.isOver = true;
            } else {
                broadcastNotification(command.getGameID(),
                        (next == ChessGame.TeamColor.WHITE ? gs.whitePlayer : gs.blackPlayer) + " is in check", null);
            }
        } else if (gs.game.isInStalemate(next)) {
            broadcastNotification(command.getGameID(),
                    (next == ChessGame.TeamColor.WHITE ? gs.whitePlayer : gs.blackPlayer) + " is in stalemate", null);
            gs.isOver = true;
        }
    }

    private void broadcastMessage(int gameID, ServerMessage msg) throws IOException {
        Set<Session> removeList = new HashSet<>();
        for (Session c : sessions.getSessionsForGame(gameID)) {
            if (c.isOpen()) {
                c.getRemote().sendString(new Gson().toJson(msg));
            } else {
                removeList.add(c);
            }
        }
        for (Session s : removeList) {
            sessions.removeSessionFromGame(gameID, s);
        }
    }

    private void broadcastNotification(int gameID, String note, Session exclude) throws IOException {
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, note);
        Set<Session> removeList = new HashSet<>();
        for (Session c : sessions.getSessionsForGame(gameID)) {
            if (!c.isOpen()) {
                removeList.add(c);
                continue;
            }
            if (c == exclude) {
                continue;
            }
            c.getRemote().sendString(new Gson().toJson(notification));
        }
        for (Session s : removeList) {
            sessions.removeSessionFromGame(gameID, s);
        }
    }

    private void sendError(Session session, String msg) throws IOException {
        ServerMessage err = new ServerMessage(msg);
        session.getRemote().sendString(new Gson().toJson(err));
    }
}
