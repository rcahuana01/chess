package websocket.commands;

import chess.ChessGame;

public class Connect extends UserGameCommand{
    public final ChessGame.TeamColor teamColor;
    public final boolean observer;

    public Connect(String authToken, Integer gameID, ChessGame.TeamColor teamColor, boolean observer) {
        super(CommandType.CONNECT, authToken, gameID);
        this.teamColor = teamColor;
        this.observer = observer;
    }
}