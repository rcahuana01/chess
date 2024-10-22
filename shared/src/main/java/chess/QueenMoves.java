package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class QueenMoves implements PlaceMovesCalculator {
    private final int[][] queenMoves = {
            {1, 0},   // Down
            {-1, 0},  // Up
            {0, 1},   // Right
            {0, -1},  // Left
            {1, 1},   // Down-Right
            {1, -1},  // Down-Left
            {-1, 1},  // Up-Right
            {-1, -1}  // Up-Left
    };

    @Override
    public String toString() {
        return "QueenMoves{" +
                "queenMoves=" + Arrays.toString(queenMoves) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        QueenMoves that = (QueenMoves) o;
        return Objects.deepEquals(queenMoves, that.queenMoves);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(queenMoves);
    }

    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int curRow = position.getRow();
        int curCol = position.getColumn();
//        System.out.println("CurrentPos Rook = (" + curRow + "," + curCol + ")");
        printBoard(board, position);

        for (int[] moves : queenMoves) {
            int rowStep = moves[0];
            int colStep = moves[1];
            int newRow = curRow;
            int newCol = curCol;
            while (isWithinLimits(newRow + rowStep, newCol + colStep)) {
                newRow += rowStep;
                newCol += colStep;
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece pieceNewPos = board.getPiece(newPosition);
                ChessPiece curPiece = board.getPiece(position);

                if (pieceNewPos == null) {
                    validMoves.add(new ChessMove(position, newPosition, null));
//                    System.out.println("Valid move to empty square: " + newPosition);
                } else if (curPiece.getTeamColor() != pieceNewPos.getTeamColor()) {
                    validMoves.add(new ChessMove(position, newPosition, null));
//                    System.out.println("Captured Enemy at = (" + newRow + "," + newCol + ")");
                    break;
                } else {
//                    System.out.println("Invalid move blocked by friend: " + newPosition);
                    break;
                }

            }
        }
        return validMoves;
    }

    private boolean isWithinLimits(int row, int col) {
        return (row > 0 && row <= 8) && (col > 0 && col <= 8);
    }

    private void printBoard(ChessBoard board, ChessPosition kingPosition) {
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 8; col++) {
                if (row == kingPosition.getRow() - 1 && col == kingPosition.getColumn() - 1) {
                    System.out.print('k');
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
        System.out.println();

    }
}
