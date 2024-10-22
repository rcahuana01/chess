package chess;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractPieceMoves implements PlaceMovesCalculator {

    protected boolean isWithinLimits(int row, int col) {
        return (row > 0 && row <= 8) && (col > 0 && col <= 8);
    }

    protected Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position, int[][] moveDirections) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int curRow = position.getRow();
        int curCol = position.getColumn();
        ChessPiece currentPiece = board.getPiece(position);

        for (int[] direction : moveDirections) {
            int rowStep = direction[0];
            int colStep = direction[1];
            int newRow = curRow;
            int newCol = curCol;
            while (isWithinLimits(newRow + rowStep, newCol + colStep)) {
                newRow += rowStep;
                newCol += colStep;
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null) {
                    validMoves.add(new ChessMove(position, newPosition, null));
                } else if (currentPiece.getTeamColor() != pieceAtNewPosition.getTeamColor()) {
                    validMoves.add(new ChessMove(position, newPosition, null));
                    break;
                } else {
                    break;
                }
            }
        }
        return validMoves;
    }
}
