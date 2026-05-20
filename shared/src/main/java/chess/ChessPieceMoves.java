package chess;

import java.util.ArrayList;
import java.util.Collection;
import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;
/**
 * Calculates the Positions the Chess piece can move to
 */
public class ChessPieceMoves {
    static ChessPiece.PieceType[] promotionTypes = {QUEEN, BISHOP, KNIGHT, ROOK};
    static int boardLength = 8; //side length of chessboard

    public ChessPieceMoves(){}

    static public Collection<ChessMove> getPieceMoves(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type,
                                                      ChessPosition position, ChessBoard board){
        Collection<ChessMove> possibleMoves = new ArrayList<>(); //arraylist of possible moves for the piece
        ChessGame.TeamColor opponent = WHITE;
        if(pieceColor == WHITE) {
            opponent = BLACK;
        }

        if(type==PAWN){
            pawnMoves(board, possibleMoves, opponent, position);
        }
        if(type == ROOK || type == QUEEN){
            rookMoves(board, possibleMoves, opponent, position);
        }
        if(type == KNIGHT){
            knightMoves(board, possibleMoves, opponent, position);
        }
        if(type == BISHOP || type == QUEEN){
            bishopMoves(board, possibleMoves, opponent, position);
        }
        if(type == KING){
            kingMoves(board, possibleMoves, opponent, position);
        }
        return possibleMoves;
    }

    /**
     * Function that adds move to possibleMoves if where move ends is null or has an opponent piece. returns
     * * true if ends in opponent piece. If promotion is true and valid, adds promotion options
     *
     * @param temp          Chess position move ends at
     * @param board         current ChessBoard
     * @param possibleMoves collection of possible moves for the piece on the board
     * @param opp           color of opposing team
     * @param position      current position of piece
     * @param promotion     if true, consider promotion options (recommended to put in (row == promotionRow) as promotion)
     * @param capture       if true, the piece can capture the piece at the end of the move
     * @return true if move ends in opponent piece, false if ends in open space
     */
    private static boolean addMoveIfOpen(ChessPosition temp, ChessBoard board,
                                         Collection<ChessMove> possibleMoves, ChessGame.TeamColor opp,
                                         ChessPosition position, boolean promotion, boolean capture){
        if(board.getPiece(temp) == null){
            if(promotion){
                for (ChessPiece.PieceType t : promotionTypes) {
                    possibleMoves.add(new ChessMove(position, temp, t));
                }
            } else {
                possibleMoves.add(new ChessMove(position, temp, null));
            }
            return false;
        } else if (capture && (board.getPiece(temp)).getTeamColor() == opp){
            if(promotion){
                for (ChessPiece.PieceType t : promotionTypes) {
                    possibleMoves.add(new ChessMove(position, temp, t));
                }
            } else {
                possibleMoves.add(new ChessMove(position, temp, null));
            }
        }
        return true;
    }

    /**
     * adds pawn moves to possibleMoves
     *
     * @param board current chess board
     * @param possibleMoves collection of all possible moves of pawn
     * @param opponent color of opposing team
     * @param position current position of piece
     */
    private static void pawnMoves(ChessBoard board,
                                  Collection<ChessMove> possibleMoves, ChessGame.TeamColor opponent,
                                  ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();
        int promotionRow = 2; //the row that black pawn can promote from
        int startingRow = 7; //starting row for black pawn
        int pawnForward = -1; //value that is moving forward for a black pawn
        if(opponent == BLACK) { //team color is white
            pawnForward = 1;
            startingRow = 2; //starting row for white pawn
            promotionRow = 7; //promotion row for white pawn
        }
        int moveRow = row + pawnForward; //row moves to
        //move forward 1, including promotion
        if(moveRow > 0 && moveRow <= boardLength) {
            addMoveIfOpen(new ChessPosition(moveRow, col), board, possibleMoves, opponent, position, promotionRow == row, false);
        } // move 2
        if(row == startingRow) {
            if (board.getPiece(new ChessPosition(moveRow, col)) == null) {
                addMoveIfOpen(new ChessPosition(moveRow + pawnForward, col), board, possibleMoves, opponent, position, false, false);
            }
        } //diagonal capture left
        if (row < boardLength && row > 1 && col > 1 && board.getPiece(new ChessPosition(moveRow, col - 1)) != null) {
            addMoveIfOpen(new ChessPosition(moveRow, col - 1), board, possibleMoves, opponent, position, promotionRow == row, true);
        } //diagonal capture right
        if (row < boardLength && row > 1 && col < boardLength && board.getPiece(new ChessPosition(moveRow, col + 1)) != null) {
            addMoveIfOpen(new ChessPosition(moveRow, col + 1), board, possibleMoves, opponent, position, promotionRow == row, true);
        }
    }

    /**
     * adds rook (and queen) moves to possibleMoves
     *
     * @param board current chess board
     * @param possibleMoves collection of all possible moves of pawn
     * @param opponent color of opposing team
     * @param position current position of piece
     */
    private static void rookMoves(ChessBoard board, Collection<ChessMove> possibleMoves, ChessGame.TeamColor opponent, ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();
        for(int r = row + 1; r <= boardLength; r++){ //check every square vertically up from position
            if(addMoveIfOpen(new ChessPosition(r, col), board, possibleMoves, opponent, position, false, true)){
                break;
            }
        }
        for(int r = row - 1; r > 0; r--){ //check every square vertically down from position
            if(addMoveIfOpen(new ChessPosition(r, col), board, possibleMoves, opponent, position, false, true)){
                break;
            }
        }
        for(int c = col + 1; c <= boardLength; c++){ //check every square horizontally right from position
            if(addMoveIfOpen(new ChessPosition(row, c), board, possibleMoves, opponent, position, false, true)){
                break;
            }
        }
        for(int c = col - 1; c > 0; c--){ //check every square horizontally left from position
            if(addMoveIfOpen(new ChessPosition(row, c), board, possibleMoves, opponent, position, false, true)){
                break;
            }
        }
    }

    /**
     * adds knight moves to possibleMoves
     *
     * @param board current chess board
     * @param possibleMoves collection of all possible moves of pawn
     * @param opponent color of opposing team
     * @param position current position of piece
     */
    private static void knightMoves(ChessBoard board, Collection<ChessMove> possibleMoves, ChessGame.TeamColor opponent, ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();
        if (row < 7){ // check forward jumps
            if(col < 8) { //forward 2, right 1
                addMoveIfOpen(new ChessPosition(row + 2, col + 1), board, possibleMoves, opponent, position, false, true);
            }
            if(col > 1){ //forward 2, left 1
                addMoveIfOpen(new ChessPosition(row + 2, col - 1), board, possibleMoves, opponent, position, false, true);
            }
        }
        if(row > 2){ // check backwards jumps
            if(col < 8) { //backwards 2, right 1
                addMoveIfOpen(new ChessPosition(row - 2, col + 1), board, possibleMoves, opponent, position, false, true);
            }
            if(col > 1){ //backwards 2, left 1
                addMoveIfOpen(new ChessPosition(row - 2, col - 1), board, possibleMoves, opponent, position, false, true);
            }
        }
        if(col > 2){ //check left jumps
            if(row < 8){ //jump left 2, forward 1
                addMoveIfOpen(new ChessPosition(row + 1, col - 2), board, possibleMoves, opponent, position, false, true);
            }
            if(row > 1){ //jump left 2, backward 1
                addMoveIfOpen(new ChessPosition(row-1, col -2), board, possibleMoves, opponent, position, false, true);
            }
        }
        if(col < 7){ // check right jumps
            if(row < 8){ //forward 1, right 2
                addMoveIfOpen(new ChessPosition(row + 1, col + 2), board, possibleMoves, opponent, position, false, true);
            }
            if(row > 1){ //backward 1, right 2
                addMoveIfOpen(new ChessPosition(row-1, col+2), board, possibleMoves, opponent, position, false, true);
            }
        }
    }

    /**
     * adds bishop (and queen) moves to possibleMoves
     *
     * @param board current chess board
     * @param possibleMoves collection of all possible moves of pawn
     * @param opponent color of opposing team
     * @param position current position of piece
     */
    private static void bishopMoves(ChessBoard board, Collection<ChessMove> possibleMoves, ChessGame.TeamColor opponent, ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        for(int l = 1; l < col; l++){ // diagonal up left
            if(row + l <= boardLength){
                if(addMoveIfOpen(new ChessPosition(row + l, col - l), board, possibleMoves, opponent, position, false, true)){
                    break;
                }
            }
        }
        for(int l = 1; l < col; l++){ // diagonal down left
            if(row - l > 0){
                if(addMoveIfOpen(new ChessPosition(row - l, col - l), board, possibleMoves, opponent, position, false, true)){
                    break;
                }
            }
        }
        //diagonal to right and up
        for(int l = 1; l <= boardLength - col; l++){ // diagonal up right
            if(row + l <= boardLength){
                if(addMoveIfOpen(new ChessPosition(row + l, col + l), board, possibleMoves, opponent, position, false, true)){
                    break;
                }
            }
        }
        for(int l = 1; l <= 8-col; l++){ // diagonal down right
            if(row - l > 0){
                if(addMoveIfOpen(new ChessPosition(row - l, col + l), board, possibleMoves, opponent, position, false, true)){
                    break;
                }
            }
        }
    }

    /**
     * adds king moves to possibleMoves
     *
     * @param board current chess board
     * @param possibleMoves collection of all possible moves of pawn
     * @param opponent color of opposing team
     * @param position current position of piece
     */
    private static void kingMoves(ChessBoard board, Collection<ChessMove> possibleMoves, ChessGame.TeamColor opponent, ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();
        if(row < boardLength){ // move forward
            addMoveIfOpen(new ChessPosition(row + 1, col), board, possibleMoves, opponent, position, false, true);
            if(col < 8){ // move diagonal up right
                addMoveIfOpen(new ChessPosition(row + 1, col + 1), board, possibleMoves, opponent, position, false, true);
            }
            if(col > 1){ // move diagonal up left
                addMoveIfOpen(new ChessPosition(row + 1, col - 1), board, possibleMoves, opponent, position, false, true);
            }
        }
        if(row > 1){ // move down
            addMoveIfOpen(new ChessPosition(row - 1, col), board, possibleMoves, opponent, position, false, true);
            if(col < 8){ // move diagonal down right
                addMoveIfOpen(new ChessPosition(row - 1, col + 1), board, possibleMoves, opponent, position, false, true);
            }
            if(col > 1){ // move diagonal down left
                addMoveIfOpen(new ChessPosition(row - 1, col - 1), board, possibleMoves, opponent, position, false, true);
            }
        }
        if(col < 8){ // move straight right
            addMoveIfOpen(new ChessPosition(row, col + 1), board, possibleMoves, opponent, position, false, true);
        }
        if(col > 1){ // move straight left
            addMoveIfOpen(new ChessPosition(row, col - 1), board, possibleMoves, opponent, position, false, true);
        }
    }
}