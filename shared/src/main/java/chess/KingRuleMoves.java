package chess;

import java.util.ArrayList;
import java.util.List;

public class KingRuleMoves {
    public List<ChessMove> KingRuleMoves(ChessPosition position, ChessPiece piece, ChessBoard board) {
        int[][] kingDirections = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        List<ChessMove> validMoves = new ArrayList<>();

        for (int i = 0; i < kingDirections.length; i++) {
            int newRow = position.getRow() + kingDirections[i][0];
            int newCol = position.getColumn() + kingDirections[i][1];
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
