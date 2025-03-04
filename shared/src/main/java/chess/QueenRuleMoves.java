package chess;

import java.util.ArrayList;
import java.util.List;

public class QueenRuleMoves extends PieceMoves{
    public List<ChessMove> queenRuleMoves(ChessPosition position, ChessPiece piece, ChessBoard board) {
        int[][] queenDirections = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        return makeMove(position, piece, board, queenDirections);

    }
}
