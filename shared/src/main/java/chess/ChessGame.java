package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor.*;
import static chess.ChessGame.TeamStatus.*;
import static chess.ChessPiece.PieceType.*;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board;
    TeamColor turn;
    TeamStatus White;
    TeamStatus Black;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn = WHITE;
        White = PLAYING;
        Black = PLAYING;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public enum TeamStatus {
        PLAYING,
        IN_CHECK,
        IN_CHECKMATE,
        STALEMATE,
        WIN,
        LOSS
    }
    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if(board.getPiece(startPosition) == null){
            return null;
        }
        TeamColor pieceColor = board.getPiece(startPosition).getTeamColor();
        Collection<ChessMove> validMoves = new ArrayList<>();  //array to hold only valid moves
        ChessGame testGame = new ChessGame();
        ChessBoard testBoard = new ChessBoard(board);
        for(var move : board.getPiece(startPosition).pieceMoves(board, startPosition)){
            testBoard.addPiece(move.getEndPosition(), testBoard.getPiece(startPosition));
            testBoard.addPiece(move.getStartPosition(), null);
            testGame.setBoard(testBoard);
            testGame.setTeamTurn(pieceColor);
            if(!testGame.isInCheck(pieceColor) && !testGame.isInCheckmate(pieceColor)){
                validMoves.add(move);
            }
            testBoard = new ChessBoard(board);
        }
        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        boolean valid = false;
        TeamColor teamColor = null;
        // if there is a piece at the starting position
        if((move != null) && (board.getPiece(move.getStartPosition()) != null)) {
            // if the piece at the starting position is the same color as whose turn it is
            teamColor = board.getPiece(move.getStartPosition()).getTeamColor();
            if(turn == teamColor) {
                // for all the valid moves that can be made by that piece at the starting position
                for (var m : validMoves(move.getStartPosition())) {
                    if (m.equals(move)) {
                        valid = true;
                        break;
                    }
                }
            }
        }
        if(valid && (board.getPiece(move.getStartPosition()) != null)){
            ChessPiece temp = new ChessPiece(teamColor, board.getPiece(move.getStartPosition()).getPieceType() );
            if(move.getPromotionPiece() == null){
                board.addPiece(move.getEndPosition(), temp);
            } else{
                temp = new ChessPiece(teamColor, move.getPromotionPiece());
                board.addPiece(move.getEndPosition(), temp);
            }
            board.addPiece(move.getStartPosition(), null);

            if(teamColor == WHITE){
                turn = BLACK;
            } else{
                turn = WHITE;
            }
        } else{
            throw new InvalidMoveException("Invalid Move");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        setStatus(teamColor);
        if(teamColor == WHITE) return(White == IN_CHECK);
        return ( Black == IN_CHECK);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        setStatus(teamColor);
        if(teamColor == WHITE){
            return(White == IN_CHECKMATE);
        }
        return ( Black == IN_CHECKMATE);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        setStatus(teamColor);
        if(teamColor == WHITE){
            return(White == STALEMATE);
        }
        return ( Black == STALEMATE);
    }

    /**
     * Sets this game's chessboard to a given board
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

    /**
     * Gets status of teamColor
     *
     * @param teamColor which team to return status.
     * @return TeamStatus
     */
    public TeamStatus getStatus(TeamColor teamColor){
        if(teamColor == WHITE) return White;
        return Black;
    }

    /**
     * Sets teamColor's team status if applicable to check, checkmate, or stalemate
     *
     * @param teamColor which team to check for check, checkmate, stalemate.
     * @return void
     */
    public void setStatus(TeamColor teamColor){
        TeamColor opponent;
        if(teamColor == WHITE) {
            opponent = BLACK;
            White = PLAYING;
        } else{
            opponent = WHITE;
            Black = PLAYING;
        }
        boolean kingCantMove = false;
        //check for check
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                if(board.getPiece(new ChessPosition(r, c)) != null) {
                    ChessPiece p = board.getPiece(new ChessPosition(r, c));
                    if (p.getTeamColor() == opponent) {
                        for (var m : p.pieceMoves(board, new ChessPosition(r, c))) {
                            if ((board.getPiece(m.getEndPosition()) != null) && (board.getPiece(m.getEndPosition()).getPieceType() == KING)) {
                                if(teamColor == WHITE) White = IN_CHECK;
                                else if(teamColor == BLACK) Black = IN_CHECK;
                            }
                        }
                    }
                    //check for checkmate
                    if (p.getTeamColor() == teamColor && p.getPieceType() == KING) {
                        if (p.pieceMoves(board, new ChessPosition(r, c)).isEmpty()) {
                            kingCantMove = true;
                        }
                    }
                }
            }
        }
        if(teamColor == WHITE && White == IN_CHECK && kingCantMove) White = IN_CHECKMATE;
        if(teamColor == BLACK && Black == IN_CHECK && kingCantMove) Black = IN_CHECKMATE;
        //check for stalemate
        if(!(getStatus(teamColor)==IN_CHECK) && !(getStatus(teamColor)==IN_CHECKMATE)) {
            //change status to stalemate, change back to playing if found to have moves available
            if (teamColor == WHITE) White = STALEMATE;
            else if (teamColor == BLACK) Black = STALEMATE;
            //check every piece on this team
            for (int r = 1; r <= 8; r++) {
                for (int c = 1; c <= 8; c++) {
                    if (board.getPiece(new ChessPosition(r, c)) != null) {
                        if (board.getPiece(new ChessPosition(r, c)).getTeamColor() == teamColor) {
                            //if any of this team pieces can make valid move, not in stalemate
                            if (!board.getPiece(new ChessPosition(r, c)).pieceMoves(board, new ChessPosition(r, c)).isEmpty()) {
                                if (teamColor == WHITE) White = PLAYING;
                                else if (teamColor == BLACK) Black = PLAYING;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(getBoard(), chessGame.getBoard()) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBoard(), turn);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", turn=" + turn +
                '}';
    }
}
