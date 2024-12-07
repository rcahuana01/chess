package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoves extends AbstractPieceMovesKing {
    private final int[][] knightMoves = {
            {2, 1},   // Down
            {1, 2},  // Up
            {-1, 2},   // Right
            {-2, 1},  // Left
            {-2, -1},   // Down-Right
            {-1, -2},  // Down-Left
            {2, -1},  // Up-Right
            {1, -2}  // Up-Left
    };
    @Override
    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition position) {
        return calculate(board, position, knightMoves);
    }
}
