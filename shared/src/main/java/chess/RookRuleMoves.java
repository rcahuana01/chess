package chess;

import java.util.ArrayList;
import java.util.List;

public class RookRuleMoves extends PieceMoves{
    public List<ChessMove> rookRuleMoves(ChessPosition position, ChessPiece piece, ChessBoard board) {
        int[][] rookDirections = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        return makeMove(position, piece, board, rookDirections);


    }
}
