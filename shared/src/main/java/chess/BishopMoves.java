package chess;

import java.util.Collection;

public class BishopMoves extends AbstractPieceMoves {
    private final int[][] bishopDirections = {
            {1, 1},   // Down-Right
            {1, -1},  // Down-Left
            {-1, 1},  // Up-Right
            {-1, -1}  // Up-Left
    };

    @Override
    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition position) {
        return calculateMoves(board, position, bishopDirections);
    }
}
