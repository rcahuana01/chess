package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
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

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        try {
            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(command, session);
                case LEAVE -> handleLeave(command, session);
                case RESIGN -> handleResign(command, session);
                case MAKE_MOVE -> {
                    MakeMoveCommand makeMoveCmd = new Gson().fromJson(message, MakeMoveCommand.class);
                    handleMakeMove(makeMoveCmd, session);
                }
            }
        } catch (DataAccessException ex) {
            sendError(session, "Error: " + ex.getMessage());
        }
    }

    private void handleConnect(UserGameCommand command, Session session) throws IOException, DataAccessException {
        if (!gameStates.containsKey(command.getGameID())) {
            GameState gs = new GameState();
            if ("WHITE".equals(command.getAuthToken())) gs.whitePlayer = "WHITE";
            else if ("BLACK".equals(command.getAuthToken())) gs.blackPlayer = "BLACK";
            else gs.observers.add(command.getAuthToken());
            gameStates.put(command.getGameID(), gs);
        }

        GameState gameState = gameStates.get(command.getGameID());
        if (gameState.isOver) {
            throw new DataAccessException("Game is over, cannot connect");
        }
        String user = command.getAuthToken();
        if (!Objects.equals(gameState.whitePlayer, user) &&
                !Objects.equals(gameState.blackPlayer, user) &&
                !gameState.observers.contains(user)) {
            if (gameState.whitePlayer == null && "WHITE".equals(user)) {
                gameState.whitePlayer = user;
            } else if (gameState.blackPlayer == null && "BLACK".equals(user)) {
                gameState.blackPlayer = user;
            } else {
                gameState.observers.add(user);
            }
        }

        sessions.addSessionToGame(command.getGameID(), session);
        ServerMessage loadGameMsg = new ServerMessage(gameState.game);
        session.getRemote().sendString(new Gson().toJson(loadGameMsg));
        String note = user + " has connected to game " + command.getGameID();
        broadcastNotification(command.getGameID(), note, session);
    }

    private void handleLeave(UserGameCommand command, Session session) throws DataAccessException, IOException {
        if (!gameStates.containsKey(command.getGameID())) {
            throw new DataAccessException("Invalid game ID for leave");
        }
        GameState gs = gameStates.get(command.getGameID());
        if (gs.isOver) {
        }
        sessions.removeSessionFromGame(command.getGameID(), session);

        String user = command.getAuthToken();
        String note = user + " left the game " + command.getGameID();
        broadcastNotification(command.getGameID(), note, session);
    }

    private void handleResign(UserGameCommand command, Session session) throws DataAccessException, IOException {
        if (!gameStates.containsKey(command.getGameID())) {
            throw new DataAccessException("Invalid game ID for resign");
        }
        GameState gs = gameStates.get(command.getGameID());
        if (gs.isOver) {
            throw new DataAccessException("Game is already over, cannot resign");
        }
        String user = command.getAuthToken();
        if (!Objects.equals(gs.whitePlayer, user) && !Objects.equals(gs.blackPlayer, user)) {
            throw new DataAccessException("Observers cannot resign");
        }
        gs.isOver = true;
        String note = user + " resigned from game " + command.getGameID();
        broadcastNotification(command.getGameID(), note, null);
    }

    private void handleMakeMove(MakeMoveCommand command, Session session) throws DataAccessException, IOException {
        if (!gameStates.containsKey(command.getGameID())) {
            throw new DataAccessException("Invalid game ID for move");
        }
        GameState gs = gameStates.get(command.getGameID());
        if (gs.isOver) {
            throw new DataAccessException("Game is already over, cannot move");
        }

        String user = command.getAuthToken();
        boolean isWhite = Objects.equals(gs.whitePlayer, user);
        boolean isBlack = Objects.equals(gs.blackPlayer, user);
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

        String note = user + " made a move: " + move;
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
