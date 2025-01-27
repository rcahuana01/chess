package chess;

import chess.ChessGame.TeamColor;

import java.util.ArrayList;
import java.util.List;

public class PawnRuleMoves {
    public List<ChessMove> PawnRuleMoves(ChessPosition position, ChessPiece piece, ChessBoard board) {
        List<ChessMove> validMoves = new ArrayList<>();
        int direction = (piece.getTeamColor() == TeamColor.WHITE) ? -1 : 1;
        int promotionRow = (piece.getTeamColor() == TeamColor.WHITE) ? 7 : 2;
        int curRow = position.getRow();
        int curCol = position.getColumn();

        for (int i=0;i<8;i++){
            ChessPosition endPosition = new ChessPosition(curRow+direction, curCol);
            ChessPosition endPosition2 = new ChessPosition(curRow*2, curCol);
            if (isWithinLimits(curRow+direction, curCol)){
                ChessMove newMove = new ChessMove(position, endPosition, null);
                ChessMove newMove2 = new ChessMove(position, endPosition2, null);
                validMoves.add(newMove);
                validMoves.add(newMove2);
            }
        }


        ChessPosition promotion = new ChessPosition(position.getRow(), position.getColumn());
        ChessMove promotionMove = new ChessMove(position, promotion, ChessPiece.PieceType.QUEEN);
        ChessMove promotionMove = new ChessMove(position, promotion, ChessPiece.PieceType.ROOK);
        ChessMove promotionMove = new ChessMove(position, promotion, ChessPiece.PieceType.BISHOP);
        ChessMove promotionMove = new ChessMove(position, promotion, ChessPiece.PieceType.KNIGHT);

        ChessMove newMove3 = new ChessMove(position, endPosition3, promotionMove);
        validMoves.add(newMove3);


        return validMoves;
    }

    boolean isWithinLimits(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}
