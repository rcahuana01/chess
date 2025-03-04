package chess;

import java.util.ArrayList;
import java.util.List;

public class KingRuleMoves extends PieceMoves1 {
    public List<ChessMove> getKingMoves(ChessPosition position, ChessPiece piece, ChessBoard board) {
        int[][] kingDirections = {{1, 1}, {1, -1}, {1, 0}, {-1, 0}, {-1, 1}, {-1, -1}, {0, 1}, {0, -1}};
        return makeMove(position, piece, board, kingDirections);
    }
}

