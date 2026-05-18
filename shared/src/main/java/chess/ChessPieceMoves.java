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

    public ChessPieceMoves(){}

    static public Collection<ChessMove> getPieceMoves(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type,
                                                      ChessPosition position, ChessBoard board){
        Collection<ChessMove> possibleMoves = new ArrayList<>(); //arraylist of possible moves for the piece
        int row = position.getRow();
        int col = position.getColumn();
        int boardLength = 8; //side length of chessboard
        int pawnForward = -1; //value that is moving forward for a black pawn
        int promotionRow = 2; //the row that black pawn can promote from
        int startingRow = 7; //starting row for black pawn
        int moveRow; //row moving to
        ChessPosition temp;
        ChessGame.TeamColor opponent = WHITE;
        if(pieceColor == WHITE) {
            pawnForward = 1;
            startingRow = 2; //starting row for white pawn
            promotionRow = 7; //promotion row for white pawn
            opponent = BLACK;
        }

        if(type==PAWN){
            moveRow = row + pawnForward;
            //move no promotion
            if(moveRow > 0 && moveRow < boardLength && row != promotionRow) {
                temp = new ChessPosition(moveRow, col);
                if (board.getPiece(temp) == null) {
                    possibleMoves.add(new ChessMove(position, temp, null));
                }
            } //move with promotion options
            else if (row == promotionRow){
                temp = new ChessPosition(moveRow, col);
                if (board.getPiece(temp) == null) {
                    for(ChessPiece.PieceType t : promotionTypes){
                        possibleMoves.add(new ChessMove(position, temp, t));
                    }
                }
            } // move 2
            if(row == startingRow) {
                temp = new ChessPosition(moveRow + pawnForward, col);
                if (board.getPiece(temp) == null && board.getPiece(new ChessPosition(moveRow, col)) == null) {
                    possibleMoves.add(new ChessMove(position, temp, null));
                }
            } //diagonal capture left
            if (row < boardLength && row > 1 && col > 1) {
                temp = new ChessPosition(moveRow, col - 1);
                if (board.getPiece(temp) != null && (board.getPiece(temp)).getTeamColor() == opponent) {
                    if (row == promotionRow) { //diagonal promotion
                        for (ChessPiece.PieceType t : promotionTypes) {
                            possibleMoves.add(new ChessMove(position, temp, t));
                        }
                    } else {
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
            } //diagonal capture right
            if (row < boardLength && row > 1 && col < boardLength) {
                temp = new ChessPosition(moveRow, col + 1);
                if (board.getPiece(temp) != null && (board.getPiece(temp)).getTeamColor() == opponent) {
                    if (row == promotionRow) { //diagonal promotion
                        for (ChessPiece.PieceType t : promotionTypes) {
                            possibleMoves.add(new ChessMove(position, temp, t));
                        }
                    } else {
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
            }
        }
        if(type == ROOK || type == QUEEN){
            for(int r = row + 1; r <= boardLength; r++){ //check every square vertically up from position
                if(addMoveIfOpen(new ChessPosition(r, col), board, possibleMoves, opponent, position)){
                    break;
                }
            }
            for(int r = row - 1; r > 0; r--){ //check every square vertically down from position
                if(addMoveIfOpen(new ChessPosition(r, col), board, possibleMoves, opponent, position)){
                    break;
                }
            }
            for(int c = col + 1; c <= boardLength; c++){ //check every square horizontally right from position
                if(addMoveIfOpen(new ChessPosition(row, c), board, possibleMoves, opponent, position)){
                    break;
                }
            }
            for(int c = col - 1; c > 0; c--){ //check every square horizontally left from position
                if(addMoveIfOpen(new ChessPosition(row, c), board, possibleMoves, opponent, position)){
                    break;
                }
            }
        }
        if(type == KNIGHT){
            if (row < 7){ // check forward jumps
                if(col < 8) { //forward 2, right 1
                    addMoveIfOpen(new ChessPosition(row + 2, col + 1), board, possibleMoves, opponent, position);
                }
                if(col > 1){ //forward 2, left 1
                    addMoveIfOpen(new ChessPosition(row + 2, col - 1), board, possibleMoves, opponent, position);
                }
            }
            if(row > 2){ // check backwards jumps
                if(col < 8) { //backwards 2, right 1
                    addMoveIfOpen(new ChessPosition(row - 2, col + 1), board, possibleMoves, opponent, position);
                }
                if(col > 1){ //backwards 2, left 1
                    addMoveIfOpen(new ChessPosition(row - 2, col - 1), board, possibleMoves, opponent, position);
                }
            }
            if(col > 2){ //check left jumps
                if(row < 8){ //jump left 2, forward 1
                    addMoveIfOpen(new ChessPosition(row + 1, col - 2), board, possibleMoves, opponent, position);
                }
                if(row > 1){ //jump left 2, backward 1
                    addMoveIfOpen(new ChessPosition(row-1, col -2), board, possibleMoves, opponent, position);
                }
            }
            if(col < 7){ // check right jumps
                if(row < 8){ //forward 1, right 2
                    addMoveIfOpen(new ChessPosition(row + 1, col + 2), board, possibleMoves, opponent, position);
                }
                if(row > 1){ //backward 1, right 2
                    addMoveIfOpen(new ChessPosition(row-1, col+2), board, possibleMoves, opponent, position);
                }
            }
        }
        if(type == BISHOP || type == QUEEN){
            for(int l = 1; l < col; l++){ // diagonal up left
                if(row + l <= boardLength){
                    if(addMoveIfOpen(new ChessPosition(row + l, col - l), board, possibleMoves, opponent, position)){
                        break;
                    }
                }
            }
            for(int l = 1; l < col; l++){ // diagonal down left
                if(row - l > 0){
                    if(addMoveIfOpen(new ChessPosition(row - l, col - l), board, possibleMoves, opponent, position)){
                        break;
                    }
                }
            }
            //diagonal to right and up
            for(int l = 1; l <= boardLength - col; l++){ // diagonal up right
                if(row + l <= boardLength){
                    if(addMoveIfOpen(new ChessPosition(row + l, col + l), board, possibleMoves, opponent, position)){
                        break;
                    }
                }
            }
            for(int l = 1; l <= 8-col; l++){ // diagonal down right
                if(row - l > 0){
                    if(addMoveIfOpen(new ChessPosition(row - l, col + l), board, possibleMoves, opponent, position)){
                        break;
                    }
                }
            }
        }
        if(type == KING){
            if(row < boardLength){ // move forward
                addMoveIfOpen(new ChessPosition(row + 1, col), board, possibleMoves, opponent, position);
                if(col < 8){ // move diagonal up right
                    addMoveIfOpen(new ChessPosition(row + 1, col + 1), board, possibleMoves, opponent, position);
                }
                if(col > 1){ // move diagonal up left
                    addMoveIfOpen(new ChessPosition(row + 1, col - 1), board, possibleMoves, opponent, position);
                }
            }
            if(row > 1){ // move down
                addMoveIfOpen(new ChessPosition(row - 1, col), board, possibleMoves, opponent, position);
                if(col < 8){ // move diagonal down right
                    addMoveIfOpen(new ChessPosition(row - 1, col + 1), board, possibleMoves, opponent, position);
                }
                if(col > 1){ // move diagonal down left
                    addMoveIfOpen(new ChessPosition(row - 1, col - 1), board, possibleMoves, opponent, position);
                }
            }
            if(col < 8){ // move straight right
                addMoveIfOpen(new ChessPosition(row, col + 1), board, possibleMoves, opponent, position);
            }
            if(col > 1){ // move straight left
                addMoveIfOpen(new ChessPosition(row, col - 1), board, possibleMoves, opponent, position);
            }
        }
        return possibleMoves;
    }

    /**
     * Function that adds move to possibleMoves if where move ends is null or has an opponent piece. returns
     *      * true if ends in opponent piece
     * @param temp Chessposition move ends in
     * @param board current ChessBoard
     * @param possibleMoves collection of possible moves for the piece on the board
     * @param opp color of opposing team
     * @param position current position of piece
     * @return true if move ends in opponent piece, false if ends in open space
     */
    private static boolean addMoveIfOpen(ChessPosition temp, ChessBoard board,
                                  Collection<ChessMove> possibleMoves, ChessGame.TeamColor opp,
                                  ChessPosition position){
        if(board.getPiece(temp) == null){
            possibleMoves.add(new ChessMove(position, temp, null));
            return false;
        } else {
            if ((board.getPiece(temp)).getTeamColor() == opp){
                possibleMoves.add(new ChessMove(position, temp, null));
            }
        }
        return true;
    }

}
 //make helper function that checks if null or enemy there and adds chess position