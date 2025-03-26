package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.StringJoiner;

import static chess.ChessPiece.PieceType.*;
import static ui.EscapeSequences.*;

public class Graphics {

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE = 3;

    // Padded characters.
    private static final String EMPTY = "   ";
    private static final ChessPiece piece = null;
    private static final String[] HEADERS = { "a", "b", "c", "d", "e", "f", "g", "h" };
    public static ChessBoard gameBoard;


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

//        drawSquare(out, SET_TEXT_COLOR_WHITE, WHITE_BISHOP);
        drawBoard(out);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }



    private static void drawSquare(PrintStream out, String bgColor, String content) {
        out.print(bgColor);
        if (content !=null && !content.isBlank()){
            int side = (SQUARE_SIZE - 1) / 2;
            out.print(" ".repeat(side));
            out.print(content);
            out.print(" ".repeat(side));

        } else {
            out.print(" ".repeat(SQUARE_SIZE));
        }
        out.print(RESET_TEXT_COLOR);
    }



    private static void drawBoard(PrintStream out) {
        // Top header
        drawSquare(out, SET_BG_COLOR_LIGHT_GREY, null);
        for (String h : HEADERS) drawSquare(out, SET_BG_COLOR_LIGHT_GREY, h);
        drawSquare(out, SET_BG_COLOR_LIGHT_GREY, null);
        out.println(RESET_BG_COLOR);

        ChessPiece[][] board = gameBoard.getBoard();

        // Each rank
        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; row++) {
            String rankLabel = String.valueOf(8 - row);
            drawSquare(out, SET_BG_COLOR_LIGHT_GREY, rankLabel);

            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                boolean isLight = (row + col) % 2 == 0;
                String bg = isLight ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;

                ChessPiece piece = board[row][col];
                String symbol = (piece == null) ? null : getSymbol(piece);
                drawSquare(out, bg, symbol);
            }

            drawSquare(out, SET_BG_COLOR_LIGHT_GREY, rankLabel);
            out.println(RESET_BG_COLOR);
        }

        // Bottom header
        drawSquare(out, SET_BG_COLOR_LIGHT_GREY, null);
        for (String h : HEADERS) drawSquare(out, SET_BG_COLOR_LIGHT_GREY, h);
        drawSquare(out, SET_BG_COLOR_LIGHT_GREY, null);
        out.println(RESET_BG_COLOR);
    }

    public static String getSymbol(ChessPiece piece) {
        switch (piece.getPieceType()) {
            case KING:   return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KING   : BLACK_KING;
            case QUEEN:  return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_QUEEN  : BLACK_QUEEN;
            case ROOK:   return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_ROOK   : BLACK_ROOK;
            case BISHOP: return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT: return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KNIGHT : BLACK_KNIGHT;
            case PAWN:   return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_PAWN   : BLACK_PAWN;
            default: 	return " ";
        }
    }




}