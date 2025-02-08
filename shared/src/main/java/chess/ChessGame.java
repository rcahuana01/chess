package chess;

import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
        this.currentTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTurn == chessGame.currentTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTurn, board);
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
        if (piece != null) {
            for (ChessMove move : piece.pieceMoves(board, startPosition)) {
                ChessBoard tempBoard = board.boardCopy();
                tempBoard.addPiece(move);
                if (!new ChessGame(tempBoard, currentTurn).isInCheck(currentTurn)) {
                    validMoves.add(move);
                }
            }
            return validMoves;
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
         ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("Invalid move: not your piece.");
        }

        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Invalid move.");
        }

        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(currentTurn, move.getPromotionPiece()));
        } else {
            board.addPiece(move.getEndPosition(), piece);
        }
        board.removePiece(move.getStartPosition());

        currentTurn = (currentTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition KingPosition = findKing(teamColor);
        if (KingPosition == null) {
            return false;
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Save that piece location and apply moves in that location
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece !=null && piece.getTeamColor() != teamColor) {
                    for (ChessMove move : piece.pieceMoves(board, position)){
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
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
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
                    if (!validMoves(position).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;

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
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(position).isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;

    }


    public boolean isLegalMove(ChessMove move,ChessPosition startPosition){
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null || piece.getTeamColor() != currentTurn){
            return false;
        }

        Collection<ChessMove> possibleMoves = validMoves(startPosition);
        if (!possibleMoves.contains(move)){
            return false;
        }

        ChessBoard newBoard = board.boardCopy();
        newBoard.addPiece(move.getEndPosition(), piece);
        newBoard.removePiece(startPosition);
        ChessGame newGame = new ChessGame();
        newGame.setBoard(newBoard);
        return !newGame.isInCheck(currentTurn);
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
