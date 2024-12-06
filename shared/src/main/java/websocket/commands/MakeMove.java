package websocket.commands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand {
    public final ChessMove move;

    public MakeMove(String authToken, Integer gameId, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameId);
        this.move = move;
    }
}