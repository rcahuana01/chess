package chess;

import java.util.Collection;

public class QueenMoves extends AbstractPieceMoves {
    private final int[][] queenDirections = {
            {1, 0},   // Down
            {-1, 0},  // Up
            {0, 1},   // Right
            {0, -1},  // Left
            {1, 1},   // Down-Right
            {1, -1},  // Down-Left
            {-1, 1},  // Up-Right
            {-1, -1}  // Up-Left
    };

    @Override
    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition position) {
        return calculateMoves(board, position, queenDirections);
    }
}
