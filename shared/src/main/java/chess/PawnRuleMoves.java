package chess;

import chess.ChessGame.TeamColor;

import java.util.ArrayList;
import java.util.List;

public class PawnRuleMoves {
    public List<ChessMove> PawnRuleMoves(ChessPosition position, ChessPiece piece, ChessBoard board) {
        List<ChessMove> validMoves = new ArrayList<>();
        int direction = (piece.getTeamColor() == TeamColor.WHITE) ? -1 : 1;
        int startRow = (piece.getTeamColor() == TeamColor.WHITE) ? 6 : 1;
        int promotionRow = (piece.getTeamColor() == TeamColor.WHITE) ? 0 : 7;
        int curRow = position.getRow();
        int curCol = position.getColumn();

        ChessPosition oneStep = new ChessPosition(curRow+direction, curCol);
        if (isWithinLimits(curRow+direction, curCol) && board.getPiece(oneStep)==null) {
            validMoves.add(new ChessMove(position, oneStep, null));
            if (curRow==startRow){
                ChessPosition twoStep = new ChessPosition(curRow+2*direction,curCol);
                if(board.getPiece(twoStep)==null){
                    validMoves.add((new ChessMove(position,twoStep,null)));
                }
            }
        }

        if (piece.getTeamColor()==TeamColor.WHITE){
            int newCol = curCol + 1;
            ChessPosition diagPos = new ChessPosition(curRow+direction,newCol);
            if (isWithinLimits(diagPos.getRow(), diagPos.getColumn()) && board.getPiece(diagPos)!=null &&
                    board.getPiece(diagPos).getTeamColor() != piece.getTeamColor()) {
                validMoves.add(new ChessMove(position, diagPos,null));
                }
        }
        else {
            int newCol = curCol - 1;
            ChessPosition diagPos = new ChessPosition(curRow+direction,newCol);
            if (isWithinLimits(diagPos.getRow(), diagPos.getColumn()) && board.getPiece(diagPos)!=null &&
                    board.getPiece(diagPos).getTeamColor() != piece.getTeamColor()) {
                validMoves.add(new ChessMove(position, diagPos,null));
            }
        }
        if (curRow+direction==promotionRow){
            validMoves.add(new ChessMove(position, oneStep, ChessPiece.PieceType.ROOK));
            validMoves.add(new ChessMove(position, oneStep, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(position, oneStep, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(position, oneStep, ChessPiece.PieceType.KNIGHT));
        }


        return validMoves;
    }

    boolean isWithinLimits(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}
