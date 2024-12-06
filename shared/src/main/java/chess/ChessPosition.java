package chess;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    public ChessPosition getPositionFromString(String s, boolean blackAtBottom) {
        if(!Pattern.compile("[a-h][1-8]").matcher(s).matches()) {
            return null;
        }

        char colChar = Character.toLowerCase(s.charAt(0));
        int col = (colChar - 'a') + 1;
        int row = Character.getNumericValue(s.charAt(1));

        // Check if the row and column values are within the valid range
        if (col >= 1 && col <= 8 && row >= 1 && row <= 8) {
            if (blackAtBottom) {
                //row = 9 - row; // Invert the row if black is at the bottom
            }
            return new ChessPosition(row, col);
        } else {
            return null;
        }
    }
}
