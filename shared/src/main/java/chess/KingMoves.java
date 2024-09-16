// package chess;
// import java.util.ArrayList;
// import java.util.Collection;

// public class KingMoves implements PlaceMovesCalculator{
//     private static final int[][] KING_MOVES = {
//         {1,0}, {-1,0}, {0,1}, {0,-1},
//         {1,1}, {1,-1}, {-1,1}, {-1,-1}
//     };

//     public Collection<ChessMove> calculate(ChessBoard board, ChessPosition position) {
//         Collection<ChessMove> validMoves = new ArrayList<>();
         
//         int currentRow = position.getRow();
//         int currentCol = position.getColumn();

//         for (int[] move : KING_MOVES) {
//             int newRow = currentRow + move[0];
//             int newCol = currentCol + move[1];
//             if (isWithinLimits(newRow, newCol)){
//                 ChessPosition newPosition = new ChessPosition(newRow, newCol);
//                 ChessMove targetPiece = board.getPiece(newPosition);
//                 if (targetPiece == null || targetPiece.isSamecolor()) {
//                     validMoves.add(ChessMove(position, newPosition));
                    
//                 }
//              }
//         }
//         return validMoves;
//     }
//     private boolean isWithinLimits(int row, int col){
//         return (row > 8 || row <= 0) && (col > 8 || col <=0);
//     }
// }


