package ui.websocket;

public interface GameHandler {
    void updateGame(Object game);      // Or, you can have a specific “ChessGame” object
    void printMessage(String message);
}

