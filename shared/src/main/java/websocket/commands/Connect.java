package websocket.commands;

import chess.ChessGame;

public class Connect extends UserGameCommand{
    public final ChessGame.TeamColor teamColor;
    public final boolean observer;

    public Connect(String authToken, Integer gameId, ChessGame.TeamColor teamColor, boolean observer) {
        super(CommandType.CONNECT, authToken, gameId);
        this.teamColor = teamColor;
        this.observer = observer;
    }
}

