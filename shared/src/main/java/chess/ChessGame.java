package chess;

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
    private boolean endGame;
    private ChessBoard board;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.currentTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTurn = team;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMovesSet = new ArrayList<>();

        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }


        TeamColor teamColor = piece.getTeamColor();
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);

        for (ChessMove move : possibleMoves) {
            ChessPosition newPos = move.getEndPosition();
            if (isWithinLimits(newPos.getRow(), newPos.getColumn())) {
                ChessPiece capturedPiece = board.getPiece(newPos);
                ChessBoard tempBoard = new ChessBoard(board);
                tempBoard.addPiece(newPos, piece);
                tempBoard.addPiece(startPosition, null);
                if (!isInCheck(teamColor)) {
                    validMovesSet.add(move);
                }
                board.addPiece(startPosition, piece);
                board.addPiece(newPos, capturedPiece);
            }
        }


        return validMovesSet;
    }

    private boolean isWithinLimits(int row, int col) {
        return (row >= 1 && row <= 8) && (col >= 1 && col <= 8);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessGame chessGame = (ChessGame) o;
        return currentTurn == chessGame.currentTurn && Objects.equals(getBoard(), chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTurn, getBoard());
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();

        ChessPiece movingPiece = this.board.getPiece(startPos);
        if (movingPiece == null || movingPiece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException();
        }

        Collection<ChessMove> possibleMoves = validMoves(startPos);
        if (!possibleMoves.contains(move)) {
            throw new InvalidMoveException();
        }
        ChessPiece capturedPiece = board.getPiece(endPos);
        board.addPiece(startPos, null);
        if (move.getPromotionPiece() != null) {
            ChessPiece promotedPiece = new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(endPos, promotedPiece);
        } else {
            board.addPiece(endPos, movingPiece);
        }
        if (isInCheck(movingPiece.getTeamColor())) {
            board.addPiece(startPos, movingPiece);
            board.addPiece(endPos, capturedPiece);
            throw new InvalidMoveException();
        }
        setTeamTurn(movingPiece.getTeamColor() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);

    }

    public ChessPosition locateKing(TeamColor teamColor, ChessBoard board) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition curPos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(curPos);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return curPos;
                }
            }
        }
        return null;
    }

    public boolean hasValidMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition curPos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(curPos);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (pieceHasValidMoves(curPos, piece, teamColor)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isSafeMove(ChessPosition curPos, ChessPosition newPos, ChessPiece piece, TeamColor teamColor) {
        ChessPiece capturedPiece = board.getPiece(newPos);
        board.addPiece(curPos, null);
        board.addPiece(newPos, piece);

        boolean isSafe = !isInCheck(teamColor);

        // Restore the board to the original state
        board.addPiece(curPos, piece);
        board.addPiece(newPos, capturedPiece);

        return isSafe;
    }

    public boolean pieceHasValidMoves(ChessPosition curPos, ChessPiece piece, TeamColor teamColor) {
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, curPos);
        for (ChessMove move : possibleMoves) {
            if (isSafeMove(curPos, move.getEndPosition(), piece, teamColor)) {
                return true;
            }
        }
        return false;
    }

    private boolean canAttackPosition(ChessPosition currentPos, ChessPiece piece, ChessPosition targetPos) {
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, currentPos);
        for (ChessMove move : possibleMoves) {
            if (move.getEndPosition().equals(targetPos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = locateKing(teamColor, board);

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(currentPosition);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    if (canAttackPosition(currentPosition, piece, kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && !hasValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
       /* The player's king is not threatened by any other pieces
        The player cannot move to any other square without putting their king in check
        None of the player's other pieces can make a legal move to save the king */
        return !isInCheck(teamColor) && !hasValidMoves(teamColor);

    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public void setEndGame() {
        endGame = true;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }


}
