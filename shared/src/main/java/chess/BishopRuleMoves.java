package chess;

import java.util.ArrayList;
import java.util.List;

public class BishopRuleMoves extends PieceMoves{
    public List<ChessMove> bishopRuleMoves(ChessPosition position, ChessPiece piece, ChessBoard board) {
        int[][] bishopDirections = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        return makeMove(position, piece, board, bishopDirections);

    }
}
