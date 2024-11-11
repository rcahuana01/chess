package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;

import static ui.EscapeSequences.RESET_BG_COLOR;

public class ChessBoardBuilder {
    private static ChessBoard gameBoard;
    private static ChessGame chessGame;
    public ChessBoardBuilder(ChessBoard board, ChessGame game){
        gameBoard = board;
        chessGame = game;
    }

    public void drawBoard(PrintStream out, boolean reversed, ChessPosition piecePosition) {
        ChessPiece[][] board = gameBoard.getBoard();
        String[] labels = reversed ? new String[]{" h ", " g ", " f ", " e "," d "," c "," b "," a "} :
                 new String[]{" a "," b "," c "," d "," e "," f "," g "," h " };
        String[] leftRightBorder = new String[]{" 1 "," 2 "," 3 "," 4 "," 5 "," 6 "," 7 "," 8 "};

        letterBorder(out, topBottomBorder);

        if (reversed) {
            for (int i = 0; i < 8; i++) {
                fillBoard
            }
    }
    }

    public void fillBoard(PrintStream out, String rowLabel, ChessPiece[] row, boolean isEvenRow, boolean reversed, ChessPosition piecePosition, int ind) {
        Collection<ChessPosition> endPositions = new HashSet<>();
        endPositions.add(piecePosition);
        if (piecePosition != null) {
            Collection<ChessMove> validMoves = chessGame.validMoves(piecePosition);
            for (ChessMove move : validMoves) {
                endPositions.add(move.getEndPosition());
            }
        }
        numberBorder(out, rowLabel);

        String currentColor = isEvenRow ? SET_BG_COLOR_BEIGE : SETBG_COLOR_BROWN;
        String alternateColor = isEvenRow ? SET_BG_COLOR_BROWN : SET_BG_COLOR_BEIGE;
        for (int i = 0; i < row.length; i++) {
            ChessPiece piece = row[reversed ? 7 - i : i];
            ChessPosition currentPosition = new ChessPosition(reversed ? 8 - ind : ind + 1, reversed ? 8 - i : i + 1);

            if (endPositions.contains(currentPosition)) {
                out.print(SET_BG_COLOR_YELLOW);
                printPiece(out, null);
            } else {
                out.print(currentColor);
                printPiece(out, piece);
            }

            String temp = currentColor;
            currentColor = alternateColor;
            alternateColor = temp;
        }

        numberBorder(out, rowLabel);
        out.println(RESET_BG_COLOR);
    }

    public static void letterBorder(PrintStream out, String[] labels) {
        out.print(SET_TEXT_COLOR_LIGHT_GREY + SET_BG_COLOR_BLACK + EMPTY);
        for (String label : labels) {
            out.print(label + "\u202F");
        }
        out.println(EMPTY + RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    public static void numberBorder(PrintStream out, String label){
        out.print(SET_TEXT_COLOR_LIGHT_GREY + SET_BG_COLOR_BLACK + label);
    }

    public static void printPiece(PrintStream out, ChessPiece piece){
        if (piece != null) {
            if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                out.print(SET_TEXT_COLOR_WHITE);
                switch (piece.getPieceType())
            }
        }
    }
    
    public void printBoard(String playerColor, ChessPosition piecePosition) {
        var out = new PrintStream((System.out, true, StandardCharsets.UTF_8));
        
        if (playerColor.equals("BLACK")) {
            drawBoard(out, true, piecePosition);
        } else {
            drawBoard(out, false, piecePosition);
        }
        out.print(RESET_BG_COLOR + RESET_TEST_COLOR);
    }
}
