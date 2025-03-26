package ui;

import chess.ChessPiece;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.StringJoiner;

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
        // Top border

        for (int i = 0; i < 10; i++){
            drawSquare(out, SET_BG_COLOR_LIGHT_GREY, null);
            for (int j = 1; j < 9; j++){
                drawSquare(out, SET_TEXT_COLOR_BLACK, headers[i]);
            }
        }
        for (int i = 1; i < 9; i++){
            drawSquare(out, SET_TEXT_COLOR_BLACK, headers[i]);
        }

        out.println(RESET_BG_COLOR);
        // Middle
        for (int i = 0;i < 8;i++){
            drawSquare(out, SET_BG_COLOR_LIGHT_GREY, null);
            for (int j=0;j < 8;j++){
                boolean light = (i + j) % 2 == 0;
                String bg = light ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;
                drawSquare(out, bg, null);

            }
            drawSquare(out, SET_BG_COLOR_LIGHT_GREY, null);
            out.println(RESET_BG_COLOR);
        }
        // Bottom border
        for (int i = 0; i < 10; i++){
            drawSquare(out, SET_BG_COLOR_LIGHT_GREY, null);
        }
        out.println(RESET_BG_COLOR);
    }

    private static void printLabels(PrintStream out){
        out.println("   ");
        String[] headers = { "a", "b", "c", "d", "e", "f", "g", "h" };

        for (int letter = headers[0].charAt(0);letter <= headers.length; letter++){
            String label = String.valueOf(letter);
            out.print(center(label, SQUARE_SIZE));
        }
        out.println();
    }


}