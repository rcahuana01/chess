package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;

import static ui.EscapeSequences.*;

public class ChessBoardBuilder {
    private static ChessBoard gameBoard;
    private static ChessGame chessGame;

    public ChessBoardBuilder(ChessBoard board, ChessGame game) {
        gameBoard = board;
        chessGame = game;
    }

    public static void drawBoard(PrintStream out, boolean reversed, ChessPosition piecePosition) {
        ChessPiece[][] board = gameBoard.getBoard();

        String[] topBottomBorder = reversed
                ? new String[]{" h ", " g ", " f ", " e ", " d ", " c ", " b ", " a "} :
                new String[]{" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
        String[] leftRightBorder = new String[]{" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};

        // Top border
        letterBorder(out, topBottomBorder);

        // Main board
        if (reversed) {
            for (int i = 0; i < 8; i++) {
                fillBoard(out, leftRightBorder[i], board[i], i % 2 == 0, reversed, piecePosition, i);
            }
        } else {
            for (int i = 7; i >= 0; i--) {
                fillBoard(out, leftRightBorder[i], board[i], i % 2 != 0, reversed, piecePosition, i);
            }
        }

        // Bottom border
        letterBorder(out, topBottomBorder);
    }

    public static void fillBoard(PrintStream out, String rowLabel, ChessPiece[] row, boolean isEvenRow, boolean
            reversed, ChessPosition piecePosition, int ind) {
        Collection<ChessPosition> endPositions = new HashSet<>();
        endPositions.add(piecePosition);
        if (piecePosition != null) {
            Collection<ChessMove> validMoves = chessGame.validMoves(piecePosition);
            for (ChessMove move : validMoves) {
                endPositions.add(move.getEndPosition());
            }
        }

        numberBorder(out, rowLabel);

        String currentColor = isEvenRow ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREEN;
        String alternateColor = isEvenRow ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_LIGHT_GREY;

        for (int i = 0; i < row.length; i++) {
            ChessPiece piece = row[reversed ? 7 - i : i];
            ChessPosition currentPosition = new ChessPosition(reversed ? 8 - ind : ind + 1, reversed ? 8 - i : i + 1);

            if (endPositions.contains(currentPosition)) {
                out.print(SET_BG_COLOR_YELLOW);
                printPiece(out, null); // Print empty space for highlighted moves
            } else {
                out.print(currentColor);
                printPiece(out, piece);
            }

            out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);

            // Alternate color
            String temp = currentColor;
            currentColor = alternateColor;
            alternateColor = temp;
        }

        numberBorder(out, rowLabel);
        out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    public static void letterBorder(PrintStream out, String[] labels) {
        out.print(SET_TEXT_COLOR_LIGHT_GREY + SET_BG_COLOR_BLACK + EMPTY);
        for (String label : labels) {
            out.print(label + "\u202F");
        }
        out.println(EMPTY + RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    public static void numberBorder(PrintStream out, String label) {
        out.print(SET_TEXT_COLOR_LIGHT_GREY + SET_BG_COLOR_BLACK + label);
    }

    public static void printPiece(PrintStream out, ChessPiece piece) {
        if (piece != null) {
            if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                out.print(SET_TEXT_COLOR_WHITE);
                switch (piece.getPieceType()) {
                    case KING -> out.print(WHITE_KING);
                    case QUEEN -> out.print(WHITE_QUEEN);
                    case BISHOP -> out.print(WHITE_BISHOP);
                    case ROOK -> out.print(WHITE_ROOK);
                    case KNIGHT -> out.print(WHITE_KNIGHT);
                    case PAWN -> out.print(WHITE_PAWN);
                }
            } else {
                out.print(SET_TEXT_COLOR_BLACK);
                switch (piece.getPieceType()) {
                    case KING -> out.print(BLACK_KING);
                    case QUEEN -> out.print(BLACK_QUEEN);
                    case BISHOP -> out.print(BLACK_BISHOP);
                    case ROOK -> out.print(BLACK_ROOK);
                    case KNIGHT -> out.print(BLACK_KNIGHT);
                    case PAWN -> out.print(BLACK_PAWN);
                }
            }
        } else {
            out.print(EMPTY);
        }
    }

    public void printBoard(String playerColor, ChessPosition piecePosition) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        if (playerColor.equals("BLACK")) {
            drawBoard(out, true, piecePosition);
        } else {
            drawBoard(out, false, piecePosition);
        }
        out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
    }
}