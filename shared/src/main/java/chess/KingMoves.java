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
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
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
        printBoard(board,position);
        for (int []moves : kingMoves) {
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
     private boolean isWithinLimits (int row, int col){
         return (row >= 1 && row <= 8) && (col >= 1 && col <= 8);
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


