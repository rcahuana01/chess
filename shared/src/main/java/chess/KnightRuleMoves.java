package chess;

import java.util.ArrayList;
import java.util.List;

public class KnightRuleMoves {
    public List<ChessMove> KnightRuleMoves(ChessPosition position, ChessPiece piece, ChessBoard board) {
        int[][] knightDirections = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};

        List<ChessMove> validMoves = new ArrayList<>();
        for (int i = 0; i < knightDirections.length; i++) {
            int newRow = position.getRow() + knightDirections[i][0];
            int newCol = position.getColumn() + knightDirections[i][1];

            if (isWithinLimits(newRow, newCol)) {
                ChessPosition endPosition = new ChessPosition(newRow, newCol);
                ChessPiece endPiece = board.getPiece(endPosition);
                if (endPiece == null || piece.getTeamColor() != endPiece.getTeamColor()) {
                    validMoves.add(new ChessMove(position, endPosition, null));
                }
            }
        }
        return validMoves;

    }
    boolean isWithinLimits(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}
