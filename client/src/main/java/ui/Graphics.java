package ui;

import chess.ChessGame;
import chess.ChessPiece;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class Graphics {
    private static final int BOARD_SIZE = 8;
    private static final int SQUARE_SIZE = 3;
    private static final ChessGame game = new ChessGame();

    public static void main(String[] args) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        drawBoard(out, false);
        drawBoard(out, true);
        out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    private static void drawSquare(PrintStream out, String bgColor, String content) {
        out.print(bgColor);
        if (content != null && !content.isBlank()) {
            int total = SQUARE_SIZE;
            int pad = (total - content.length()) / 2;
            int rightPad = total - pad - content.length();
            out.print(" ".repeat(pad) + content + " ".repeat(rightPad));
        } else {
            out.print(EMPTY);
        }
        out.print(RESET_TEXT_COLOR);
    }

    private static void drawBoard(PrintStream out, boolean reversed) {
        ChessPiece[][] board = game.getBoard().getBoard();
        String[] files = reversed ? reverseFileLabels() : fileLabels();

        // Top file header
        drawSquare(out, SET_BG_COLOR_LIGHT_GREY, null);
        for (String f : files) {
            drawSquare(out, SET_BG_COLOR_LIGHT_GREY, f);
        }
        drawSquare(out, SET_BG_COLOR_LIGHT_GREY, null);
        out.println(RESET_BG_COLOR);

        for (int r = 0; r < BOARD_SIZE; r++) {
            int row = reversed ? r : BOARD_SIZE - 1 - r;
            String rank = centerLabel(String.valueOf(reversed ? r + 1 : BOARD_SIZE - r));

            drawSquare(out, SET_BG_COLOR_LIGHT_GREY, rank);
            for (int c = 0; c < BOARD_SIZE; c++) {
                int col = reversed ? BOARD_SIZE - 1 - c : c;
                boolean light = (row + col) % 2 == 0;
                String bg = light ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;
                ChessPiece piece = board[row][col];
                String symbol = (piece == null) ? null : getSymbol(piece);
                drawSquare(out, bg, symbol);
            }
            drawSquare(out, SET_BG_COLOR_LIGHT_GREY, rank);
            out.println(RESET_BG_COLOR);
        }

        // Bottom file header
        drawSquare(out, SET_BG_COLOR_LIGHT_GREY, null);
        for (String f : files) {
            drawSquare(out, SET_BG_COLOR_LIGHT_GREY, f);
        }
        drawSquare(out, SET_BG_COLOR_LIGHT_GREY, null);
        out.println(RESET_BG_COLOR);
    }

    private static String[] fileLabels() {
        String[] labels = new String[8];
        for (int i = 0; i < 8; i++) {
            labels[i] = centerLabel(String.valueOf((char) ('a' + i)));
        }
        return labels;
    }

    private static String[] reverseFileLabels() {
        String[] labels = new String[8];
        for (int i = 0; i < 8; i++) {
            labels[i] = centerLabel(String.valueOf((char) ('h' - i)));
        }
        return labels;
    }

    private static String centerLabel(String text) {
        return "\u2003" + text + " ";
    }

    public static String getSymbol(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING   -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING   : BLACK_KING);
            case QUEEN  -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN  : BLACK_QUEEN);
            case ROOK   -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK   : BLACK_ROOK);
            case BISHOP -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP);
            case KNIGHT -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT);
            case PAWN   -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN   : BLACK_PAWN);
        };
    }
}


