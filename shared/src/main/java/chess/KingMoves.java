package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class KingMoves implements PlaceMovesCalculator {

    private final int[][] kingMoves = {
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
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        KingMoves kingMoves1 = (KingMoves) o;
        return Objects.deepEquals(kingMoves, kingMoves1.kingMoves);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(kingMoves);
    }

    @Override
    public String toString() {
        return "KingMoves{" +
                "kingMoves=" + Arrays.toString(kingMoves) +
                '}';
    }

    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int curRow = position.getRow();
        int curCol = position.getColumn();
        System.out.println("CurrentPos King = (" + curRow + "," + curCol + ")");
        for (int[] moves : kingMoves) {
            int newRow = curRow + moves[0];
            int newCol = curCol + moves[1];
            if (isWithinLimits(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece pieceNewPos = board.getPiece(newPosition);
                ChessPiece curPos = board.getPiece(position);
//                System.out.println("Checking position: (" + newRow + "," + newCol + ")");
                if (pieceNewPos == null) {
                    validMoves.add(new ChessMove(position, newPosition, null));
//                    System.out.println("Valid move to empty square: " + newPosition);
                } else if (curPos.getTeamColor() != pieceNewPos.getTeamColor()) {
                    validMoves.add(new ChessMove(position, newPosition, null));
//                    System.out.println("Captured Enemy at = (" + newRow + "," + newCol + ")");
                } else {
//                    System.out.println("Invalid move blocked by friend: " + newPosition);
                }
            } else {
//                System.out.println("InvalidMove King = (" + newRow + "," + newCol + ") - out of bounds");
            }
        }
        return validMoves;
    }

    private boolean isWithinLimits(int row, int col) {
        return (row >= 1 && row <= 8) && (col >= 1 && col <= 8);
    }

}


