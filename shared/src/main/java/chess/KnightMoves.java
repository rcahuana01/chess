package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoves implements PlaceMovesCalculator{
    private final int[][] knightMoves = {
            {2, 1},   // Down
            {1, 2},  // Up
            {-1, 2},   // Right
            {-2, 1},  // Left
            {-2, -1},   // Down-Right
            {-1, -2},  // Down-Left
            {2, -1},  // Up-Right
            {1, -2}  // Up-Left
    };
    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition position) {
    Collection<ChessMove> validMoves = new ArrayList<>();
    int curRow = position.getRow();
    int curCol = position.getColumn();
//    System.out.println("CurrentPos Knight = (" + curRow + "," + curCol + ")");
    printBoard(board,position);
    for (int[]moves : knightMoves){
        int newRow = curRow + moves[0];
        int newCol = curCol + moves[1];
        if (isWithinLimits(newRow, newCol)) {
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            ChessPiece pieceNewPos = board.getPiece(newPosition);
            ChessPiece curPos = board.getPiece(position);
//            System.out.println("Checking position: (" + newRow + "," + newCol + ")");
            if (pieceNewPos == null) {
                validMoves.add(new ChessMove(position, newPosition, null));
//                System.out.println("Valid move to empty square: " + newPosition);
            } else if (curPos.getTeamColor() != pieceNewPos.getTeamColor()) {
                validMoves.add(new ChessMove(position, newPosition, null));
//                System.out.println("Captured Enemy at = (" + newRow + "," + newCol + ")");
            } else {
//                System.out.println("Invalid move blocked by friend: " + newPosition);
            }

        } else {
//            System.out.println("InvalidMove King = (" + newRow + "," + newCol + ") - out of bounds");
        }
    }



//                if (pieceNewPos == null) {
//                    validMoves.add(new ChessMove(position, newPosition, null));
//                    System.out.println("Valid move to empty square: " + newPosition);
//                } else if (curPos.getTeamColor() != pieceNewPos.getTeamColor()) {
//                    validMoves.add(new ChessMove(position, newPosition, null));
//                    System.out.println("Captured Enemy at = (" + newRow + "," + newCol + ")");
//                } else {
//                    System.out.println("Invalid move blocked by friend: " + newPosition);
//                }





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
}
