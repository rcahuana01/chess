package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;

    }
    public ChessPiece copy(){
        return new ChessPiece(this.pieceColor, this.type);
    }


    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch(type){
            case KING:
                KingRuleMoves kingMoves = new KingRuleMoves();
                return kingMoves.getKingMoves(myPosition, board.getPiece(myPosition), board);
            case KNIGHT:
                KnightRuleMoves knightMoves = new KnightRuleMoves();
                return knightMoves.KnightRuleMoves(myPosition, board.getPiece(myPosition), board);
            case BISHOP:
                BishopRuleMoves bishopMoves = new BishopRuleMoves();
                return bishopMoves.BishopRuleMoves(myPosition, board.getPiece(myPosition), board);
            case ROOK:
                RookRuleMoves rookMoves = new RookRuleMoves();
                return rookMoves.RookRuleMoves(myPosition, board.getPiece(myPosition), board);
            case QUEEN:
                return new QueenRuleMoves().QueenRuleMoves(myPosition, board.getPiece(myPosition), board);
            case PAWN:
                return new PawnRuleMoves().getPawnMoves(myPosition, board.getPiece(myPosition), board);
            default:
        }
        return null;
    }
}
