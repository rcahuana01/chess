package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;

public class ChessBoardBuilder {
    private ChessBoardBuilder gameBoard;
    private ChessGame chessGame;
    public ChessBoardBuilder(ChessBoardBuilder board, ChessGame game){
        gameBoard = board;
        chessGame = game;
    }

    public void drawBoard(PrintStream out, boolean reversed, ChessPosition piecePosition) {
        ChessPiece[][] board = gameBoard.getBoard();
        String[] labels = reversed ? new String[]{" h ", " g ", " f "}
                : new String[]{};
        printBorder(out, labels);

        for (int row = 0; row < 8; row++){
            int boardRow = reversed ? row : 7 - row;
            out.print(" " + (reversed ? row + 1 : 8 - row) + " ");

            String currentColor = (row % 2 == 0) ?
        }
    }

    public void printBorder(PrintStream out, String[] labels) {
        out.print("  ");
        for (String label : labels)
    }
}
