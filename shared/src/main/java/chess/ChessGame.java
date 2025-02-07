package chess;

import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor currentTurn;
    private ChessBoard board;
    public ChessGame() {
        this.board = new ChessBoard();
        this.currentTurn = TeamColor.WHITE;
    }


    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;

    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPosition);
        if (piece==null){
            return validMoves;
        }
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        if (moves != null){
            validMoves.addAll(moves);
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        try {
            ChessPosition position = move.getStartPosition();
            ChessPosition endPosition = move.getEndPosition();
            if (!isLegalMove(move, position)){
                throw new InvalidMoveException();
            }

            ChessPiece piece = board.getPiece(position);
            board.addPiece(endPosition, piece);
            if (currentTurn==TeamColor.WHITE) {
                currentTurn = TeamColor.BLACK;
            } else {
                currentTurn = TeamColor.WHITE;
            }
        } catch (InvalidMoveException e) {
            throw new InvalidMoveException(e.getMessage());
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition KingPosition = findKing(teamColor);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Save that piece location and apply moves in that location
                ChessPiece piece = board.getPiece(new ChessPosition(i,j));
                if (piece.getTeamColor() != teamColor) {
                    ChessPosition position = new ChessPosition(i, j);
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(board, position);
                    for (ChessMove move : possibleMoves){
                        if (move.getEndPosition().equals(KingPosition)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    private ChessPosition findKing(TeamColor teamColor) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i,j));
                if (piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return new ChessPosition(i, j);
                }

            }
        }
        return null;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // Is the king in Check?
        if (isInCheck(teamColor)){
            return true;
        }
        // Can the King move safe?
        ChessPosition KingPosition = findKing(teamColor);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(new ChessPosition(i,j));
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(board, position);
                    for (ChessMove move : possibleMoves){
                        ChessBoard newBoard = board.boardCopy();
                        newBoard.addPiece(position, piece);
                        newBoard.removePiece(position);
                        if (isInCheck(teamColor)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
        // Can any piece block the attack?

        // Can any piece capture the attacking piece?
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // The player is not in check but has no legal moves available
        if (isInCheck(teamColor)){
            return false;
        }
        // Check if the player has any legal moves
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i,j));
                if (piece != null && piece.getTeamColor() != teamColor) {
                    ChessPosition position = new ChessPosition(i, j);
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(board, position);
                    for (ChessMove move : possibleMoves){
                        ChessBoard newBoard = board.boardCopy();
                        newBoard.addPiece(position, piece);
                        newBoard.removePiece(position);
                        if (isLegalMove(move, position)){
                            return true;
                        }
                    }

                }
            }
        }
        return true;

    }


    public boolean isLegalMove(ChessMove move,ChessPosition startPosition){
        if(startPosition == null){
            return true;
        }
        ChessPiece piece = board.getPiece(startPosition);
        if (currentTurn.equals(getTeamTurn())){
            return true;
        }
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        if (!possibleMoves.contains(move)){
            return false;
        }
        ChessPiece endPiece = board.getPiece(move.getEndPosition());
        if (endPiece != null && endPiece.getTeamColor()!=piece.getTeamColor()){
            return false;
        }
        ChessBoard newBoard = board.boardCopy();
        isInCheck(getTeamTurn());
        return false;
    }
    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
