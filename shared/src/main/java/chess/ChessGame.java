package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;
    private boolean endGame;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.endGame = false;
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }
    public boolean isGameOver() {
        return endGame;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    public boolean getEndGame(){
        return endGame;
    }

    public void setEndGame(){
        endGame = true;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    private boolean validateMove(ChessMove possibleMove){
        ChessPosition startPosition = possibleMove.getStartPosition();
        ChessPiece currentPiece = board.getPiece(startPosition);

        //make a copy of starter board
        ChessBoard starterBoard = new ChessBoard();
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                if (board.getPiece(new ChessPosition(j+1, i+1)) != null){
                    starterBoard.addPiece(new ChessPosition(j+1, i+1), board.getPiece(new
                            ChessPosition(j+1, i+1)));
                }
            }
        }

        //try move
        board.removePiece(startPosition);
        board.addPiece(possibleMove.getEndPosition(), currentPiece);

        boolean isValidMove = !isInCheck(currentPiece.getTeamColor());

        board.removePiece(possibleMove.getEndPosition());
        board.addPiece(startPosition, currentPiece);

        board = starterBoard;

        return isValidMove;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new HashSet<>();
        ChessPiece currentPiece = board.getPiece(startPosition);
        if (currentPiece != null) {
            Collection<ChessMove> possibleMoves = currentPiece.pieceMoves(board, startPosition);
            for (ChessMove possibleMove : possibleMoves){
                if (validateMove(possibleMove)){
                    validMoves.add(possibleMove);
                }
            }
        }
        return validMoves;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        makeMove(move, teamTurn);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move, TeamColor player) throws InvalidMoveException {
        if (endGame){
            throw new InvalidMoveException("Game is over :(");
        }
        if(player != getTeamTurn()) {
            throw new InvalidMoveException("It is the other team's turn");
        }

        if (board.getPiece((move.getStartPosition())) != null) {
            TeamColor pieceColor = board.getPiece(move.getStartPosition()).getTeamColor();
            Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
            if(pieceColor != teamTurn) {
                throw new InvalidMoveException("You cannot move another player's pieces");
            }
            if ((validMoves == null) || !(validMoves.contains(move))) {
                throw new InvalidMoveException("Invalid Move");
            }
            if (validMoves.contains(move)) {
                if (move.getPromotionPiece() != null) {
                    ChessPiece promotedPiece = new ChessPiece(teamTurn, move.getPromotionPiece());
                    board.addPiece(move.getEndPosition(), promotedPiece);
                } else {
                    board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
                }
                board.removePiece(move.getStartPosition());
            }
            //change the team turn
            if(this.teamTurn == TeamColor.WHITE){
                this.teamTurn = TeamColor.BLACK;
            } else{
                this.teamTurn = TeamColor.WHITE;
            }
        }
        else{
            throw new InvalidMoveException("Piece does not exist");
        }
    }

    private ChessPosition kingPosition(TeamColor teamColor){
        ChessPosition kingPosition = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition position = new ChessPosition(i+1, j+1);
                if ((board.getPiece(position) != null) && (board.getPiece(position).getPieceType() ==
                        ChessPiece.PieceType.KING) && (board.getPiece(position).getTeamColor() == teamColor)) {
                    kingPosition = position;
                }
            }
        }
        return kingPosition;
    }
    private Collection<ChessMove> getMoves(TeamColor teamColor, boolean opponent){
        Collection<ChessMove> moves = new HashSet<>();
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                ChessPiece piece = board.getPiece(new ChessPosition(i+1, j+1));
                if (opponent){
                    if (piece != null && piece.getTeamColor() != teamColor) {
                        moves.addAll(piece.pieceMoves(board, new ChessPosition(i + 1, j + 1)));
                    }
                }else{
                    if (piece != null && piece.getTeamColor() == teamColor) {
                        moves.addAll(validMoves(new ChessPosition(i + 1, j + 1)));
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> opponentMoves = getMoves(teamColor, true);
        ChessPosition kingPosition = kingPosition(teamColor);
        for (ChessMove move : opponentMoves){
            if (move.getEndPosition().equals(kingPosition)){
                return true;}
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
        //if not in check, false
        if (!isInCheck(teamColor)){
            return false;
        }
        //if there still valid moves (validMoves is not empty), false
        Collection<ChessMove> teamMoves = getMoves(teamColor, false);
        if (!(teamMoves.isEmpty())){
            return false;
        }
        //else true?
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
        //if in check, false
        if (isInCheck(teamColor)){
            return false;
        }
        //if there still valid moves (validMoves is not empty), false
        Collection<ChessMove> teamMoves = getMoves(teamColor, false);
        if (!(teamMoves.isEmpty())){
            return false;
        }
        //else true?
        return true;
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

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", board=" + board +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()){ return false;}
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }

}