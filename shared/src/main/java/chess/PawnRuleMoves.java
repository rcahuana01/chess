package chess;

import chess.ChessGame.TeamColor;
import java.util.ArrayList;
import java.util.List;

public class PawnRuleMoves {

    public List<ChessMove> PawnRuleMoves(ChessPosition position, ChessPiece piece, ChessBoard board) {
        List<ChessMove> validMoves = new ArrayList<>();
        int direction = (piece.getTeamColor() == TeamColor.BLACK) ? -1 : 1;
        int startRow = (piece.getTeamColor() == TeamColor.BLACK) ? 7 : 2;
        int promotionRow = (piece.getTeamColor() == TeamColor.BLACK) ? 1 : 8;
        int curRow = position.getRow();
        int curCol = position.getColumn();

        ChessPosition oneStep = new ChessPosition(curRow + direction, curCol);
        if (isWithinLimits(oneStep) && board.getPiece(oneStep) == null) {
            if (oneStep.getRow() == promotionRow) {
                addPromotionMoves(validMoves, position, oneStep);
            } else {
                validMoves.add(new ChessMove(position, oneStep, null));
            }

            if (curRow == startRow) {
                ChessPosition twoStep = new ChessPosition(curRow + 2 * direction, curCol);
                if (isWithinLimits(twoStep) && board.getPiece(twoStep) == null) {
                    validMoves.add(new ChessMove(position, twoStep, null));
                }
            }
        }

        int[][] diagonalMoves = {{1, -1}, {1, 1}};
        for (int[] move : diagonalMoves) {
            int diagRow = curRow + direction * move[0];
            int diagCol = curCol + move[1];
            ChessPosition diagonalPosition = new ChessPosition(diagRow, diagCol);

            if (isWithinLimits(diagonalPosition)) {
                ChessPiece pieceDiagPos = board.getPiece(diagonalPosition);

                if (pieceDiagPos != null && pieceDiagPos.getTeamColor() != piece.getTeamColor()) {
                    if (diagonalPosition.getRow() == promotionRow) {
                        addPromotionMoves(validMoves, position, diagonalPosition);
                    } else {
                        validMoves.add(new ChessMove(position, diagonalPosition, null));
                    }
                }
            }
        }


        return validMoves;
    }

    private void addPromotionMoves(List<ChessMove> validMoves, ChessPosition startPosition, ChessPosition endPosition) {
        validMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN));
        validMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK));
        validMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP));
        validMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT));
    }

    private boolean isWithinLimits(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return (row >= 1 && row <= 8) && (col >= 1 && col <= 8);
    }

}
