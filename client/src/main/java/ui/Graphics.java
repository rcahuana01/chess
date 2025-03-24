//package ui;
//
//import java.io.PrintStream;
//import java.nio.charset.StandardCharsets;
//
//import static ui.EscapeSequences.ERASE_SCREEN;
//import static ui.EscapeSequences.*;
//
//public class Graphics {
//    private static final int BOARD_SIZE_IN_SQUARES = 8;
//    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 8;
//    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;
//
//    public static void main(String[] args) {
//        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
//
//        out.print(ERASE_SCREEN);
//
//        drawHeaders(out);
//
//        drawTicTacToeBoard(out);
//
//        out.print(SET_BG_COLOR_BLACK);
//        out.print(SET_TEXT_COLOR_WHITE);
//    }
//
//    private static void drawHeader(PrintStream out, String headerText) {
//        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
//        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;
//
//        out.print(EMPTY.repeat(prefixLength));
//        printHeaderText(out, headerText);
//        out.print(EMPTY.repeat(suffixLength));
//    }
//
//    private static void drawTicTacToeBoard(PrintStream out, boolean perspective) {
//
//        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
//
//            drawRowOfSquares(out);
//
//            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
//                // Draw horizontal row separator.
//                drawHorizontalLine(out);
//                setBlack(out);
//            }
//        }
//    }
//}
