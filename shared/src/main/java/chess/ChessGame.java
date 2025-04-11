package chess;

import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.List;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        board.resetBoard();

    }


    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    public void applyMove(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (move.getPromotionPiece() != null){
            board.addPiece(move.getEndPosition(), new ChessPiece(teamTurn, move.getPromotionPiece()));
        } else {
            board.addPiece(move.getEndPosition(), piece);
        }
        board.removePiece(move.getStartPosition());
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
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
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
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
                ChessBoard newBoard = board.boardCopy();
                ChessPiece movedPiece = newBoard.getPiece(move.getStartPosition());
                if (movedPiece == null) {
                    continue;
                }
                newBoard.removePiece(move.getStartPosition());
                newBoard.addPiece(move.getEndPosition(), movedPiece);

                if (!isInCheckWithBoard(piece.getTeamColor(), newBoard)) {
                    validMoves.add(move);
                }
            }
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
        if (piece == null || piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException();
        }

        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException();
        }

        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(teamTurn, move.getPromotionPiece()));
        } else {
            board.addPiece(move.getEndPosition(), piece);
        }
        board.removePiece(move.getStartPosition());

        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheckWithBoard(teamColor, board);
    }

    private boolean isInCheckWithBoard(TeamColor teamColor, ChessBoard boardState) {
        ChessPosition kingPosition = findKing(teamColor, boardState);
        if (kingPosition == null) {
            return false;
        }
        List<ChessPiece> enemyPieces = getEnemyPieces(teamColor, boardState);

        for (ChessPiece piece : enemyPieces) {
            ChessPosition piecePosition = findPiecePosition(piece, boardState);
            if (canPieceAttackKing(piece, kingPosition, boardState, piecePosition)) {
                return true;
            }
        }

        return false;
    }

    private List<ChessPiece> getEnemyPieces(TeamColor teamColor, ChessBoard boardState) {
        List<ChessPiece> enemyPieces = new ArrayList<>();

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = boardState.getPiece(pos);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    enemyPieces.add(piece);
                }
            }
        }

        return enemyPieces;
    }

    private boolean canPieceAttackKing(ChessPiece piece, ChessPosition kingPosition, ChessBoard boardState,
                                       ChessPosition piecePosition) {
        for (ChessMove move : piece.pieceMoves(boardState, piecePosition)) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    private ChessPosition findPiecePosition(ChessPiece targetPiece, ChessBoard boardState) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = boardState.getPiece(pos);
                if (piece == targetPiece) {
                    return pos;
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
        if (!isInCheck(teamColor)) { return false; }
        return iterateBoard(teamColor);
    }

    private boolean iterateBoard(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(pos).isEmpty()) {
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
        if (isInCheck(teamColor)) { return false; }
        return iterateBoard(teamColor);
    }

    private ChessPosition findKing(TeamColor teamColor, ChessBoard boardState) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = boardState.getPiece(pos);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return pos;
                }
            }
        }
        return null;
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
        return board;
    }
}
