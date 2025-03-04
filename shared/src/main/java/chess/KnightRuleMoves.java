package chess;

import java.util.ArrayList;
import java.util.List;

public class KnightRuleMoves extends PieceMoves1 {
    public List<ChessMove> knightRuleMoves(ChessPosition position, ChessPiece piece, ChessBoard board) {
        int[][] knightDirections = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
        return makeMove(position, piece, board, knightDirections);
    }

}
