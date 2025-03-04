package chess;

import java.util.ArrayList;
import java.util.List;

public abstract class PieceMoves1 {
    public List<ChessMove> makeMove(ChessPosition position, ChessPiece piece, ChessBoard board, int[][] directions) {

        List<ChessMove> validMoves = new ArrayList<>();
            for (int i = 0; i < directions.length; i++) {
            int newRow = position.getRow() + directions[i][0];
            int newCol = position.getColumn() + directions[i][1];

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
        return row >= 1 && row < 9 && col >= 1 && col < 9;
    }
}
