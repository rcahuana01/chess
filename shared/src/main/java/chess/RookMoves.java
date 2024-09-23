package chess;

import java.util.*;

public class RookMoves implements PlaceMovesCalculator{
    @Override
    public String toString() {
        return "RookMoves{" +
                "rookMoves=" + Arrays.toString(rookMoves) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RookMoves rookMoves1 = (RookMoves) o;
        return Objects.deepEquals(rookMoves, rookMoves1.rookMoves);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(rookMoves);
    }

    private final int[][] rookMoves = {
            {1, 0},   // Right
            {-1, 0},  // Left
            {0, 1},   // Down
            {0, -1},  // Up
    };
    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int curRow = position.getRow();
        int curCol = position.getColumn();
        System.out.println("CurrentPos Rook = (" + curRow + "," + curCol + ")");
        printBoard(board, position);

        for (int []moves : rookMoves) {
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
                    System.out.println("Valid move to empty square: " + newPosition);
                } else if (curPiece.getTeamColor() != pieceNewPos.getTeamColor()) {
                    validMoves.add(new ChessMove(position, newPosition, null));
                    System.out.println("Captured Enemy at = (" + newRow + "," + newCol + ")");
                    break;
                } else {
                    System.out.println("Invalid move blocked by friend: " + newPosition);
                    break;
                }

            }
        }


        return validMoves;


    }

    private boolean isWithinLimits(int row, int col){
        return (row > 0 && row <= 8) && (col > 0 && col <= 8);
    }
    private void printBoard(ChessBoard board, ChessPosition kingPosition){
        for (int row=7; row >= 0; row--){
            for (int col=0; col < 8;col++){
                if (row == kingPosition.getRow()-1 && col == kingPosition.getColumn()-1){
                    System.out.print('k');
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
        System.out.println();

    }
    //     private int moveStraight (ChessBoard board, ChessPosition position,int rowStep, int colStep, int steps){
//         Collection<ChessMove> validMoves = new ArrayList<>();
//         int currentRow = position.getRow();
//         int currentCol = position.getColumn();
//         for (int i = 1; i <= steps; i++) {
//             int newRow = currentRow + i * rowStep;
//             int newCol = currentRow + i * colStep;
//             if (isWithinLimits(newRow, newCol)) {
//                 validMoves.add(new ChessPosition(newRow, newCol));
//             }
//         }
//         return validMoves;
//
//
//     }
}
