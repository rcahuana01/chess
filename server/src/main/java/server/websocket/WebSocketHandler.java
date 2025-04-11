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
import java.util.*;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager sessions = new ConnectionManager();
    private static final Map<Integer, GameState> gameStates = new HashMap<>();
    private final SQLUserDAO userDao;
    private final SQLGameDAO gameDao;
    private final SQLAuthDAO authDao;

    {
        try {
            userDao = new SQLUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

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

        if (!username.equals(gameState.whitePlayer) &&
                !username.equals(gameState.blackPlayer) &&
                !gameState.observers.contains(username)) {
            if (gameState.whitePlayer == null && username.equals(gameData.whiteUsername())) {
                gameState.whitePlayer = username;
            } else if (gameState.blackPlayer == null && username.equals(gameData.blackUsername())) {
                gameState.blackPlayer = username;
            } else {
                gameState.observers.add(username);
            }
        }

        sessions.addSessionToGame(command.getGameID(), session);
        ServerMessage loadGameMsg = new ServerMessage(gameState.game);
        session.getRemote().sendString(new Gson().toJson(loadGameMsg));
        String note = username + " has connected to game " + command.getGameID();
        broadcastNotification(command.getGameID(), note, session);
    }

    private void leave(UserGameCommand command, Session session) throws DataAccessException, IOException {
        if (!gameStates.containsKey(command.getGameID())) {
            throw new DataAccessException("Invalid game ID for leave");
        }
        GameState gameState = gameStates.get(command.getGameID());
        AuthData authData = authDao.getAuthToken(command.getAuthToken());
        String username = authData.username();

        if (username.equals(gameState.whitePlayer)) {
            gameState.whitePlayer = null;
        } else if (username.equals(gameState.blackPlayer)) {
            gameState.blackPlayer = null;
        } else {
            gameState.observers.remove(username);
        }

        sessions.removeSessionFromGame(command.getGameID(), session);
        String note = username + " left the game " + command.getGameID();
        broadcastNotification(command.getGameID(), note, session);
    }

    private void resign(UserGameCommand command, Session session) throws DataAccessException, IOException {
        if (!gameStates.containsKey(command.getGameID())) {
            throw new DataAccessException("Invalid game ID for resign");
        }
        GameState gameState = gameStates.get(command.getGameID());
        if (gameState.isOver) {
            throw new DataAccessException("Game is already over, cannot resign");
        }
        AuthData authData = authDao.getAuthToken(command.getAuthToken());
        String username = authData.username();

        if (!username.equals(gameState.whitePlayer) && !username.equals(gameState.blackPlayer)) {
            throw new DataAccessException("Observers cannot resign");
        }

        gameState.isOver = true;
        if (username.equals(gameState.whitePlayer)) {
            gameState.whitePlayer = null;
        } else if (username.equals(gameState.blackPlayer)) {
            gameState.blackPlayer = null;
        }
        String note = username + " resigned from game " + command.getGameID();
        broadcastNotification(command.getGameID(), note, null);
        // Remove the session if desired (optional):
        sessions.removeSessionFromGame(command.getGameID(), session);
    }

    private void makeMove(MakeMoveCommand command, Session session) throws DataAccessException, IOException {
        if (!gameStates.containsKey(command.getGameID())) {
            throw new DataAccessException("Invalid game ID for move");
        }
        GameState gameState = gameStates.get(command.getGameID());
        if (gameState.isOver) {
            throw new DataAccessException("Game is already over, cannot move");
        }

        AuthData authData = authDao.getAuthToken(command.getAuthToken());
        String username = authData.username();
        boolean isWhite = username.equals(gameState.whitePlayer);
        boolean isBlack = username.equals(gameState.blackPlayer);
        if (!isWhite && !isBlack) {
            throw new DataAccessException("Observer or unknown user cannot move");
        }

        ChessGame.TeamColor currentTurn = gameState.game.getTeamTurn();
        if ((currentTurn == ChessGame.TeamColor.WHITE && !isWhite) ||
                (currentTurn == ChessGame.TeamColor.BLACK && !isBlack)) {
            throw new DataAccessException("It is not your turn");
        }
        ChessMove move = command.getMove();
        try {
            gameState.game.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new DataAccessException("Invalid move");
        }

        ServerMessage loadGameMsg = new ServerMessage(gameState.game);
        broadcastMessage(command.getGameID(), loadGameMsg);

        String note = username + " made a move: " + move;
        broadcastNotification(command.getGameID(), note, session);

        ChessGame.TeamColor next = gameState.game.getTeamTurn();
        if (gameState.game.isInCheck(next)) {
            if (gameState.game.isInCheckmate(next)) {
                broadcastNotification(command.getGameID(),
                        (next == ChessGame.TeamColor.WHITE ? gameState.whitePlayer : gameState.blackPlayer) + " is in checkmate", null);
                gameState.isOver = true;
            } else {
                broadcastNotification(command.getGameID(),
                        (next == ChessGame.TeamColor.WHITE ? gameState.whitePlayer : gameState.blackPlayer) + " is in check", null);
            }
        } else if (gameState.game.isInStalemate(next)) {
            broadcastNotification(command.getGameID(),
                    (next == ChessGame.TeamColor.WHITE ? gameState.whitePlayer : gameState.blackPlayer) + " is in stalemate", null);
            gameState.isOver = true;
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
