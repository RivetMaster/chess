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

    private ChessBoard board; //chess board for the game
    private TeamColor turn; //current team whose turn it is
    private TeamStatus whiteStatus; //game status of white side
    private TeamStatus blackStatus; // game status of black side

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn = WHITE;
        whiteStatus = PLAYING;
        blackStatus = PLAYING;
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

    /**
     * Enum identifying possible team status
     */
    public enum TeamStatus {
        PLAYING,
        IN_CHECK,
        IN_CHECKMATE,
        STALEMATE,
    }
    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //if no piece at location, no valid moves / null valid moves
        if(board.getPiece(startPosition) == null){
            return null;
        }
        TeamColor pieceColor = board.getPiece(startPosition).getTeamColor(); //color of the piece
        Collection<ChessMove> validMoves = new ArrayList<>();  //array to hold only valid moves
        ChessGame testGame = new ChessGame(); //new chessGame object to see if testBoard is in check
        ChessBoard testBoard = new ChessBoard(board); //new chessboard object to test move on
        //for every move that the piece could make, test if valid
        for(var move : board.getPiece(startPosition).pieceMoves(board, startPosition)){
            // in test board make the move
            testBoard.addPiece(move.getEndPosition(), testBoard.getPiece(startPosition));
            testBoard.addPiece(move.getStartPosition(), null);
            testGame.setBoard(testBoard);
            testGame.setTeamTurn(pieceColor);
            //check if the king in the testBoard is in danger, if not, add move to validMoves.
            // Do without calling isInCheck or isInCheckmate or setStatus so that not infinite loop
            TeamColor opponent; //opponent color
            boolean add = true; //turns false if not a valid move. If stays true adds move to validMoves
            if(pieceColor == WHITE) {
                opponent = BLACK;
            } else{
                opponent = WHITE;
            }
            //check for king in danger
            //check every square on board, for every opponent piece check if they can capture king
            int r = 1;
            int c = 1;
            while(r <= 8){
                ChessPiece p = testBoard.getPiece(new ChessPosition(r, c));
                if(p != null && p.getTeamColor() == opponent) {
                    add = kingInDanger(testBoard, add, p.pieceMoves(testBoard, new ChessPosition(r, c)));
                }
                c++;
                if(c > 8){
                    c = 1;
                    r++;
                }
            }
            if(add) {
                validMoves.add(move); //add move to validMoves if king was not in danger
            }
            testBoard = new ChessBoard(board); //reset testBoard
        }
        return validMoves;
    }

    /**
     * returns false if king is currently in danger, otherwise returns add
     *
     * @param board  current chess board
     * @param add boolean value that kingInDanger is meant to update, whether to add move
     * @return new or same add value, to add or not to add move to valid moves
     */
    private boolean kingInDanger(ChessBoard board, boolean add, Collection<ChessMove> moves){
        for(var m : moves){
            if ((board.getPiece(m.getEndPosition()) != null) &&
                    (board.getPiece(m.getEndPosition()).getPieceType() == KING)) {
                return false; //if the king is in danger, the move made was not valid
            }
        }
        return add;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        boolean valid = false; //bool to hold if the move being asked to make is valid
        TeamColor teamColor = null; //hold team color of piece to move
        // if there is a piece at the starting position
        if((move != null) && (board.getPiece(move.getStartPosition()) != null)) {
            // if the piece at the starting position is the same color as whose turn it is
            teamColor = board.getPiece(move.getStartPosition()).getTeamColor();
            if(turn == teamColor) {
                // if move is a valid move, valid becomes true
                valid = validMoves(move.getStartPosition()).contains(move);
            }
        }
        //if the move is valid and there is a piece at the starting position of the move, make the move
        if(valid && (board.getPiece(move.getStartPosition()) != null)){
            ChessPiece temp = new ChessPiece(teamColor, board.getPiece(move.getStartPosition()).getPieceType() );
            if(move.getPromotionPiece() == null){
                board.addPiece(move.getEndPosition(), temp); //if there is no promotion, new piece has same piece type
            } else{
                temp = new ChessPiece(teamColor, move.getPromotionPiece()); //if promotion, new piece has new type
                board.addPiece(move.getEndPosition(), temp);
            }
            board.addPiece(move.getStartPosition(), null); //remove starting piece from board
            //change team turn
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
        checkStatus(teamColor);
        if(teamColor == WHITE) {
            return(whiteStatus == IN_CHECK || whiteStatus == IN_CHECKMATE);
        }
        return (blackStatus == IN_CHECK || blackStatus == IN_CHECKMATE);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        checkStatus(teamColor);
        if(teamColor == WHITE) {
            return(whiteStatus == IN_CHECKMATE);
        }
        return (blackStatus == IN_CHECKMATE);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        checkStatus(teamColor);
        if(teamColor == WHITE) {
            return(whiteStatus == STALEMATE);
        }
        return ( blackStatus == STALEMATE);
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
        if(teamColor == WHITE) {
            return whiteStatus;
        }
        return blackStatus;
    }

    /**
     * Sets teamColor's team status if applicable to check, checkmate, or stalemate
     *
     * @param teamColor which team to check for check, checkmate, stalemate.
     */
    public void checkStatus(TeamColor teamColor){
        TeamColor opponent; //hold opponent team color
        //reset status to default so if no change is made status is accurate
        if(teamColor == WHITE) {
            opponent = BLACK;
            whiteStatus = PLAYING;
        } else{
            opponent = WHITE;
            blackStatus = PLAYING;
        }
        int r = 1;
        int c = 1;
        boolean kingCantMove = false; //true if king cannot make any valid moves
        boolean teamCanMove = false; //true if the team DOES has valid moves it can make
        //check for check
        while(r <= 8){
            if(board.getPiece(new ChessPosition(r, c)) != null) {
                ChessPiece p = board.getPiece(new ChessPosition(r, c));
                if (p.getTeamColor() == opponent) {
                    if(!kingInDanger(board, true, validMoves(new ChessPosition(r, c)))){
                        setStatus(teamColor, IN_CHECK); //king in danger, set status to check
                    }
                }
                if(p.getTeamColor() == teamColor && !validMoves(new ChessPosition(r, c)).isEmpty()){
                    //if any piece on team can make a valid move == can take board out of check, then not in checkmate
                    teamCanMove = true;
                }
                //check if king can make any valid moves
                if (p.getTeamColor() == teamColor && p.getPieceType() == KING && validMoves(new ChessPosition(r, c)).isEmpty()) {
                    kingCantMove = true;
                }
            }
            c++;
            if(c > 8){
                c = 1;
                r++;
            }
        }
        //check for checkmate
        if(getStatus(teamColor) == IN_CHECK && kingCantMove && !teamCanMove) {
            setStatus(teamColor, IN_CHECKMATE);

        }
        //check for stalemate = not in check or checkmate but no valid moves can be made
        if(!(getStatus(teamColor)==IN_CHECK) && !(getStatus(teamColor)==IN_CHECKMATE)) {
            //change status to stalemate, change back to playing if found to have moves available
            setStatus(teamColor, STALEMATE);
            //check every piece on this team
            r = 1;
            while (r<= 8) {
                ChessPiece p = board.getPiece(new ChessPosition(r, c));
                if (p != null && p.getTeamColor() == teamColor) {
                    //if any of this team pieces can make valid move, not in stalemate
                    if (!validMoves(new ChessPosition(r, c)).isEmpty()) {
                        setStatus(teamColor, PLAYING);
                        break;
                    }
                }
                c++;
                if(c > 8){
                    c = 1;
                    r++;
                }
            }
        }
    }

    private void setStatus(TeamColor teamColor, TeamStatus status){
        if(teamColor == WHITE){
            whiteStatus = status;
        }
        blackStatus = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(getBoard(), chessGame.getBoard()) && getTeamTurn() == chessGame.getTeamTurn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBoard(), getTeamTurn());
    }

    @Override
    public String toString() {
        return getBoard() + ", turn=" + getTeamTurn();
    }
}
