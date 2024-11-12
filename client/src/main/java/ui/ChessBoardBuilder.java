package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static ui.EscapeSequences.*;

public class ChessBoardBuilder {
    private ChessBoard gameBoard;
    private ChessGame chessGame;

    public ChessBoardBuilder(ChessBoard board, ChessGame game) {
        this.gameBoard = board;
        this.chessGame = game;
    }

    private void printBorder(PrintStream out, String[] labels) {
        out.print(SET_TEXT_COLOR_LIGHT_GREY + SET_BG_COLOR_BLACK + EMPTY);
        for (String label : labels) {
            out.print(label + "\u202F");
        }
        out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    private void printPiece(PrintStream out, ChessPiece piece) {
        if (piece == null) {
            out.print(EMPTY);
            return;
        }

        String pieceColor = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK;
        out.print(pieceColor);

        String pieceSymbol = switch (piece.getPieceType()) {
            case KING -> pieceColor.equals(SET_TEXT_COLOR_WHITE) ? WHITE_KING : BLACK_KING;
            case QUEEN -> pieceColor.equals(SET_TEXT_COLOR_WHITE) ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> pieceColor.equals(SET_TEXT_COLOR_WHITE) ? WHITE_BISHOP : BLACK_BISHOP;
            case ROOK -> pieceColor.equals(SET_TEXT_COLOR_WHITE) ? WHITE_ROOK : BLACK_ROOK;
            case KNIGHT -> pieceColor.equals(SET_TEXT_COLOR_WHITE) ? WHITE_KNIGHT : BLACK_KNIGHT;
            case PAWN -> pieceColor.equals(SET_TEXT_COLOR_WHITE) ? WHITE_PAWN : BLACK_PAWN;
        };
        out.print(pieceSymbol);
    }

    public void drawBoard(PrintStream out, boolean reversed, ChessPosition piecePosition) {
        ChessPiece[][] board = gameBoard.getBoard();
        String[] labels = reversed ? new String[]{" h ", " g ", " f ", " e ", " d ", " c ", " b ", " a "} :
                new String[]{" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
        String[] rowLabels = new String[]{" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};

        printBorder(out, labels);

        for (int i = 0; i < 8; i++) {
            int rowIndex = reversed ? i : 7 - i;
            printRow(out, rowLabels[i], board[rowIndex], rowIndex, reversed, piecePosition);
        }

        printBorder(out, labels);
    }

    private void printRow(PrintStream out, String rowLabel, ChessPiece[] row, int rowIndex, boolean reversed, ChessPosition selectedPosition) {
        out.print(SET_TEXT_COLOR_LIGHT_GREY + SET_BG_COLOR_BLACK + rowLabel);

        Set<ChessPosition> highlightedPositions = getHighlightedPositions(selectedPosition);

        for (int col = 0; col < 8; col++) {
            ChessPiece piece = row[reversed ? 7 - col : col];
            ChessPosition currentPosition = new ChessPosition(rowIndex + 1, col + 1);
            boolean isHighlighted = highlightedPositions.contains(currentPosition);

            String bgColor = (rowIndex + col) % 2 == 0 ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;
            out.print(isHighlighted ? SET_BG_COLOR_WHITE : bgColor);

            printPiece(out, piece);
        }

        out.print(RESET_BG_COLOR + rowLabel + RESET_BG_COLOR);
        out.println();
    }

    private Set<ChessPosition> getHighlightedPositions(ChessPosition selectedPosition) {
        Set<ChessPosition> highlightedPositions = new HashSet<>();
        if (selectedPosition != null) {
            highlightedPositions.add(selectedPosition);
            Collection<ChessMove> validMoves = chessGame.validMoves(selectedPosition);
            for (ChessMove move : validMoves) {
                highlightedPositions.add(move.getEndPosition());
            }
        }
        return highlightedPositions;
    }

    public void printBoard(String playerColor, ChessPosition piecePosition) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        drawBoard(out, playerColor.equals("BLACK"), piecePosition);
        out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
    }
}
