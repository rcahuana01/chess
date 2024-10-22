package chess;

import java.util.Collection;

public class RookMoves extends AbstractPieceMoves {
    private final int[][] rookDirections = {
            {1, 0},   // Right
            {-1, 0},  // Left
            {0, 1},   // Down
            {0, -1},  // Up
    };

    @Override
    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition position) {
        return calculateMoves(board, position, rookDirections);
    }
}
