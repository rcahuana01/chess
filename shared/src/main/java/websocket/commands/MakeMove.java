package websocket.commands;

import chess.ChessMove;
import model.AuthData;

public class MakeMove extends UserGameCommand{

    private ChessMove move;
    public MakeMove(String authToken, Integer gameId){
        super(CommandType.MAKE_MOVE, authToken, gameId);
        this.move = move;
    }
}
