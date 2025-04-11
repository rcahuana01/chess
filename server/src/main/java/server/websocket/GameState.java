package server.websocket;

import chess.ChessGame;

import java.util.HashSet;
import java.util.Set;

public class GameState {
    public ChessGame game;
    public String whitePlayer;
    public String blackPlayer;
    public Set<String> observers;
    public boolean isOver;

    public GameState() {
        this.game = new ChessGame();
        this.observers = new HashSet<>();
        this.isOver = false;
    }
}
