package ui;

import chess.ChessPiece;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class Graphics {

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE = 3;

    // Padded characters.
    private static final String EMPTY = "   ";
    private static final ChessPiece piece = null;



    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

//        drawSquare(out);
        drawBoard(out);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }



    private static void drawSquare(PrintStream out) {
        for (int row = 0; row < SQUARE_SIZE; row++) {
                out.print(" ");
            }
            out.print(SET_BG_COLOR_BLACK);
    }


    private static void drawBoard(PrintStream out) {
        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; row++) {
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                if ((row + col) % 2 == 0) {
                    out.print(SET_BG_COLOR_WHITE);
                } else {
                    out.print(SET_BG_COLOR_BLACK);
                }
                drawSquare(out);
            }
            out.println();
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setGray(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

}