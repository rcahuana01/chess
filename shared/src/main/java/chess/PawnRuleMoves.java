package chess;

import chess.ChessGame.TeamColor;

import java.util.ArrayList;
import java.util.List;

public class PawnRuleMoves {
    public List<ChessMove> PawnRuleMoves(ChessPosition position, ChessPiece piece, ChessBoard board) {
        List<ChessMove> validMoves = new ArrayList<>();
        int direction = (piece.getTeamColor() == TeamColor.BLACK) ? -1 : 1;
        int startRow = (piece.getTeamColor() == TeamColor.BLACK) ? 6 : 1;
        int promotionRow = (piece.getTeamColor() == TeamColor.BLACK) ? 0 : 7;
        int curRow = position.getRow();
        int curCol = position.getColumn();

        ChessPosition oneStep = new ChessPosition(curRow + direction, curCol);
        if (isWithinLimits(curRow + direction, curCol) && board.getPiece(oneStep) == null) {
            validMoves.add(new ChessMove(position, oneStep, null));
            if (curRow == startRow) {
                ChessPosition twoStep = new ChessPosition(curRow + 2 * direction, curCol);
                if(isWithinLimits(twoStep.getRow(), twoStep.getColumn()) && board.getPiece(twoStep) == null && board.getPiece(oneStep) == null){
                    validMoves.add(new ChessMove(position, twoStep, null));
                }
            }
        }

        for (int newCol : new int[]{curCol - 1, curCol + 1}) {
            ChessPosition diagPos = new ChessPosition(curRow + direction, newCol);
            if (isWithinLimits(diagPos.getRow(), diagPos.getColumn()) && board.getPiece(diagPos) != null &&
                    board.getPiece(diagPos).getTeamColor() != piece.getTeamColor()) {
                validMoves.add(new ChessMove(position, diagPos, null));
            }
        }

        if ((curRow+direction)==promotionRow && board.getPiece(oneStep) == null){
            validMoves.add(new ChessMove(position, oneStep, ChessPiece.PieceType.ROOK));
            validMoves.add(new ChessMove(position, oneStep, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(position, oneStep, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(position, oneStep, ChessPiece.PieceType.KNIGHT));
        }


        return validMoves;
    }

    boolean isWithinLimits(int row, int col) {
        return row >= 1 && row < 9 && col >= 1 && col < 9;
    }
}
