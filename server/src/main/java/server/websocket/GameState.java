package server.websocket;

import chess.ChessGame;

import java.util.HashSet;
import java.util.Set;

public class GameState {
    public ChessGame game = new ChessGame();
    public String whitePlayer;
    public String blackPlayer;
    public Set<String> observers = new HashSet<>();
    public boolean isOver = false;

    public boolean isPlayer(String token) {
        return token.equals(whitePlayer) || token.equals(blackPlayer);
    }

    public boolean isObserver(String token) {
        return observers.contains(token);
    }

    public boolean isKnown(String token) {
        return isPlayer(token) || isObserver(token);
    }
}

