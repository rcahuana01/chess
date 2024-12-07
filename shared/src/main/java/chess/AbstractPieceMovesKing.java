package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractPieceMovesKing implements PlaceMovesCalculator{
    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition position, int[][]moveDirections) {
            Collection<ChessMove> validMoves = new ArrayList<>();
            int curRow = position.getRow();
            int curCol = position.getColumn();
            for (int[] moves : moveDirections) {
                int newRow = curRow + moves[0];
                int newCol = curCol + moves[1];
                if (isWithinLimits(newRow, newCol)) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece pieceNewPos = board.getPiece(newPosition);
                    ChessPiece curPos = board.getPiece(position);
                    if (pieceNewPos == null) {
                        validMoves.add(new ChessMove(position, newPosition, null));
                    } else if (curPos.getTeamColor() != pieceNewPos.getTeamColor()) {
                        validMoves.add(new ChessMove(position, newPosition, null));
                    } else {
                    }
                } else {
                }
            }
            return validMoves;
        }

    private boolean isWithinLimits(int row, int col) {
        return (row >= 1 && row <= 8) && (col >= 1 && col <= 8);
    }

}
