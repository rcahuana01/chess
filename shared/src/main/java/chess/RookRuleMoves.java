package chess;

import java.util.ArrayList;
import java.util.List;

public class RookRuleMoves {
    public List<ChessMove> RookRuleMoves(ChessPosition position, ChessPiece piece, ChessBoard board) {
        int[][] rookDirections = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        List<ChessMove> validMoves = new ArrayList<>();
        int curRow = position.getRow();
        int curCol = position.getColumn();
        for (int[] direction : rookDirections){
            int stepRow = direction[0];
            int stepCol = direction[1];
            int tempRow = curRow;
            int tempCol = curCol;
            while (isWithinLimits(tempRow+stepRow, tempCol+stepCol)) {
                tempRow += stepRow;
                tempCol += stepCol;
                ChessPosition endPosition = new ChessPosition(tempRow, tempCol);
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
