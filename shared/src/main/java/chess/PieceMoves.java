package chess;

import java.util.ArrayList;
import java.util.List;

public abstract class PieceMoves {

    public List<ChessMove> makeMove(ChessPosition position, ChessPiece piece, ChessBoard board, int[][] directions) {
        List<ChessMove> validMoves = new ArrayList<>();
        int curRow = position.getRow();
        int curCol = position.getColumn();

        for (int[] direction : directions) {
            int stepRow = direction[0];
            int stepCol = direction[1];
            int tempRow = curRow;
            int tempCol = curCol;
            while (isWithinLimits(tempRow + stepRow, tempCol + stepCol)) {
                tempRow += stepRow;
                tempCol += stepCol;
                ChessPosition endPosition = new ChessPosition(tempRow, tempCol);
                ChessPiece endPiece = board.getPiece(endPosition);
                if (endPiece == null) {
                    validMoves.add(new ChessMove(position, endPosition, null));
                } else if (piece.getTeamColor() != endPiece.getTeamColor()) {
                    validMoves.add(new ChessMove(position, endPosition, null));
                    break;
                } else if (piece.getTeamColor() == endPiece.getTeamColor()) {
                    break;
                }
            }
        }
        return validMoves;
    }

    boolean isWithinLimits(int row, int col) {
        return row >= 1 && row < 9 && col >= 1 && col < 9;
    }
}

