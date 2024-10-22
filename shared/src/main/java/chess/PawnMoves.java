package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoves implements PlaceMovesCalculator {

    @Override
    public String toString() {
        return "PawnMoves{}";
    }

    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece curPiece = board.getPiece(position);
        ChessGame.TeamColor pieceColor = curPiece.getTeamColor();
        int curRow = position.getRow();
        int curCol = position.getColumn();

        System.out.println("Calculating moves for: " + curPiece + " at (" + curRow + ", " + curCol + ")");
        printBoard(board, position);

        int direction, promotionRow;
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            direction = 1;
            promotionRow = 8;
        } else {
            direction = -1;
            promotionRow = 1;
        }

        ChessPosition forwardPosition = new ChessPosition(curRow + direction, curCol);
        if (forwardPosition.getRow() == promotionRow) {
            addPromotionMoves(validMoves, position, forwardPosition);
        } else if (isWithinLimits(forwardPosition) && board.getPiece(forwardPosition) == null) {
            validMoves.add(new ChessMove(position, forwardPosition, null));
            System.out.println("Added forward move to: (" + forwardPosition.getRow() + ", " + forwardPosition.getColumn() + ")");
        }

        int[][] diagonalMoves = {{1, -1}, {1, 1}};
        for (int[] move : diagonalMoves) {
            int diagRow = curRow + direction * move[0];
            int diagCol = curCol + move[1];
            if (isWithinLimit(diagRow, diagCol)) {
                ChessPosition diagonalPosition = new ChessPosition(diagRow, diagCol);
                ChessPiece pieceDiagPos = board.getPiece(diagonalPosition);
                if (pieceDiagPos != null && pieceDiagPos.getTeamColor() != pieceColor) {
                    if (diagonalPosition.getRow() == promotionRow) {
                        addPromotionMoves(validMoves, position, diagonalPosition);
                    } else {
                        validMoves.add(new ChessMove(position, diagonalPosition, null));
                    }
                    System.out.println("Added diagonal capture move to: (" + diagonalPosition.getRow() + ", " + diagonalPosition.getColumn() + ")");
                }
            }
        }

        if ((pieceColor == ChessGame.TeamColor.WHITE && curRow == 2) || (pieceColor == ChessGame.TeamColor.BLACK && curRow == 7)) {
            ChessPosition doubleForwardPosition = new ChessPosition(curRow + 2 * direction, curCol);
            ChessPosition intermediatePosition = new ChessPosition(curRow + direction, curCol);
            if (board.getPiece(intermediatePosition) == null && board.getPiece(doubleForwardPosition) == null) {
                validMoves.add(new ChessMove(position, doubleForwardPosition, null));
                System.out.println("Added double move to: (" + doubleForwardPosition.getRow() + ", " + doubleForwardPosition.getColumn() + ")");
            }
        }

        return validMoves;
    }

    private void addPromotionMoves(Collection<ChessMove> validMoves, ChessPosition startPosition, ChessPosition endPosition) {
        validMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN));
        validMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK));
        validMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP));
        validMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT));
        System.out.println("Added promotion moves to: (" + endPosition.getRow() + ", " + endPosition.getColumn() + ")");
    }

    private boolean isWithinLimit(int row, int col) {
        return (row >= 1 && row <= 8) && (col >= 1 && col <= 8);
    }

    private boolean isWithinLimits(ChessPosition position) {
        return isWithinLimit(position.getRow(), position.getColumn());
    }

    // Helper method to print the chess board for debugging purposes
    private void printBoard(ChessBoard board, ChessPosition pawnPosition) {
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 8; col++) {
                if (row == pawnPosition.getRow() - 1 && col == pawnPosition.getColumn() - 1) {
                    System.out.print('P');
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
