package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.*;

public class Graphics {
    private static final int BOARD_SIZE = 8;
    private static final int SQUARE_SIZE = 3;


    public static void main(String[] args) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
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

    public static void highlightMoves(PrintStream out, ChessGame game, ChessPosition from, boolean reversed) {
        int fromRow = from.getRow() - 1;
        int fromCol = from.getColumn() - 1;
        Collection<ChessMove> legalMoves = game.validMoves(from);
        ChessPiece[][] board = game.getBoard().getBoard();

        String[] files = reversed ? reverseFileLabels() : fileLabels();
        drawSquare(out, SET_BG_COLOR_LIGHT_GREY, null);
        for (String f : files) {
            drawSquare(out, SET_BG_COLOR_LIGHT_GREY, f);
        }
        drawSquare(out, SET_BG_COLOR_LIGHT_GREY, null);
        out.println(RESET_BG_COLOR);

        for (int r = 0; r < BOARD_SIZE; r++) {
            int displayRow = reversed ? r : BOARD_SIZE - 1 - r;
            String rankLabel = centerLabel(reversed ? String.valueOf(r + 1)
                    : String.valueOf(BOARD_SIZE - r));

            drawSquare(out, SET_BG_COLOR_LIGHT_GREY, rankLabel);

            for (int c = 0; c < BOARD_SIZE; c++) {
                int displayCol = reversed ? BOARD_SIZE - 1 - c : c;

                boolean isFrom      = (displayRow == fromRow && displayCol == fromCol);
                boolean isLegalDest = isLegalDestination(legalMoves, displayRow, displayCol);
                String bgColor;
                if (isFrom) {
                    bgColor = SET_BG_COLOR_GREEN;
                } else if (isLegalDest) {
                    bgColor = SET_BG_COLOR_YELLOW;
                } else {
                    bgColor = ((displayRow + displayCol) % 2 == 0)
                            ? SET_BG_COLOR_BLACK
                            : SET_BG_COLOR_WHITE;
                }

                ChessPiece piece = board[displayRow][displayCol];
                String symbol = (piece == null) ? null : getSymbol(piece);

                drawSquare(out, bgColor, symbol);
            }

            drawSquare(out, SET_BG_COLOR_LIGHT_GREY, rankLabel);
            out.println(RESET_BG_COLOR);
        }

        drawSquare(out, SET_BG_COLOR_LIGHT_GREY, null);
        for (String f : files) {
            drawSquare(out, SET_BG_COLOR_LIGHT_GREY, f);
        }
        drawSquare(out, SET_BG_COLOR_LIGHT_GREY, null);
        out.println(RESET_BG_COLOR);
    }

    private static boolean isLegalDestination(Collection<ChessMove> moves, int arrRow, int arrCol) {
        if (moves == null) return false;
        for (ChessMove move : moves) {
            int endRow = move.getEnd().getRow() - 1;   // 1-based to 0-based
            int endCol = move.getEnd().getColumn() - 1;
            if (endRow == arrRow && endCol == arrCol) {
                return true;
            }
        }
        return false;
    }



    public static void drawBoard(PrintStream out, ChessGame game, boolean reversed) {
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
                String bg = light ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
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


