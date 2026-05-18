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

    static public Collection<ChessMove> getPieceMoves(ChessGame.TeamColor pieceColor,
                                                      ChessPiece.PieceType type,
                                                      ChessPosition position,
                                                      ChessBoard board){
        Collection<ChessMove> possibleMoves = new ArrayList<>(); //arraylist of possible moves for the piece
        int row = position.getRow();
        int col = position.getColumn();
        int boardLength = 8; //side length of chessboard
        int pawnRowBlk = 7; //row number where black pawns start
        int pawnRowWht = 2; //row number where white pawns start

        int pawnForward = -1; //value that is moving forward for a black pawn
        int promotionRow = pawnRowWht; //the row that black pawn can promote from
        int startingRow = pawnRowBlk; //starting row for black pawn
        int moveRow;
        ChessPosition temp;

        ChessGame.TeamColor opponent = WHITE;
        if(pieceColor == WHITE) {
            pawnForward = 1;
            startingRow = pawnRowWht;
            promotionRow = pawnRowBlk;
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
            }
            //move 2
            if(row == startingRow) {
                temp = new ChessPosition(moveRow + pawnForward, col);
                if (board.getPiece(temp) == null && board.getPiece(new ChessPosition(moveRow, col)) == null) {
                    possibleMoves.add(new ChessMove(position, temp, null));
                }
            }
            //diagonal capture
            if (row < boardLength && row > 1 && col > 1) { // diagonal capture left
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
            }
            if (row < boardLength && row > 1 && col < boardLength) { //diagonal capture right
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
                temp = new ChessPosition(r, col);
                if(board.getPiece(temp) == null){ //add if square empty
                    possibleMoves.add(new ChessMove(position, temp, null));
                } else {
                    if ((board.getPiece(temp)).getTeamColor() == opponent){ //add iff opponent but don't add any past
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                    break;
                }
            }
            for(int r = row - 1; r > 0; r--){ //check every square vertically down from position
                temp = new ChessPosition(r, col);
                if(board.getPiece(temp) == null){ //add if square empty
                    possibleMoves.add(new ChessMove(position, temp, null));
                } else{
                    if ((board.getPiece(temp)).getTeamColor() == opponent){ //add iff opponent but don't add any past
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                    break;
                }
            }
            for(int c = col + 1; c <= boardLength; c++){ //check every square horizontally right from position
                temp = new ChessPosition(row, c);
                if(board.getPiece(temp) == null){ //add if square empty
                    possibleMoves.add(new ChessMove(position, temp, null));
                } else {
                    if ((board.getPiece(temp)).getTeamColor() == opponent){ //add iff opponent but don't add any past
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                    break;
                }
            }
            for(int c = col - 1; c > 0; c--){ //check every square horizontally left from position
                temp = new ChessPosition(row, c);
                if(board.getPiece(temp) == null){ //add if square empty
                    possibleMoves.add(new ChessMove(position, temp, null));
                } else {
                    if ((board.getPiece(temp)).getTeamColor() == opponent){ //add iff opponent but don't add any past
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                    break;
                }
            }
        }
        if(type == KNIGHT){
            if (row < pawnRowBlk){ // check forward jumps
                if(col < 7) { //forward 2, right 1
                    temp = new ChessPosition(row + 2, col + 1);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else if (board.getPiece(temp).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
                if(col > 1){ //forward 2, left 1
                    temp = new ChessPosition(row + 2, col - 1);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else if (board.getPiece(temp).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
            }
            if(row > 2){ // check backwards jumps
                if(col < 7) { //backwards 2, right 1
                    temp = new ChessPosition(row-2, col+1);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else if (board.getPiece(temp).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
                if(col > 1){ //backwards 2, left 1
                    temp = new ChessPosition(row-2, col-1);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else if (board.getPiece(temp).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
            }
            if(col > 2){ //check left jumps
                if(row < 8){ //jump left 2, forward 1
                    temp = new ChessPosition(row + 1, col - 2);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else if (board.getPiece(temp).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
                if(row > 1){ //jump left 2, backward 1
                    temp = new ChessPosition(row-1, col -2);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else if (board.getPiece(temp).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
            }
            if(col < 7){ // check right jumps
                if(row < 8){ //forward 1, right 2
                    temp = new ChessPosition(row + 1, col + 2);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else if (board.getPiece(temp).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
                if(row > 1){ //backward 1, right 2
                    temp = new ChessPosition(row-1, col+2);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else if (board.getPiece(temp).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
            }
        }
        if(type == BISHOP || type == QUEEN){
            for(int l = 1; l < col; l++){ // diagonal up left
                if(row + l <= boardLength){
                    temp = new ChessPosition(row + l, col - l);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else {
                        if ((board.getPiece(temp)).getTeamColor() == opponent){
                            possibleMoves.add(new ChessMove(position, temp, null));
                        }
                        break;
                    }
                }
            }
            for(int l = 1; l < col; l++){ // diagonal down left
                if(row - l > 0){
                    temp = new ChessPosition(row - l, col - l);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else {
                        if ((board.getPiece(temp)).getTeamColor() == opponent){
                            possibleMoves.add(new ChessMove(position, temp, null));
                        }
                        break;
                    }
                }
            }
            //diagonal to right and up
            for(int l = 1; l <= boardLength - col; l++){ // diagonal up right
                if(row + l <= boardLength){
                    temp = new ChessPosition(row + l, col + l);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else {
                        if ((board.getPiece(temp)).getTeamColor() == opponent){
                            possibleMoves.add(new ChessMove(position, temp, null));
                        }
                        break;
                    }
                }
            }
            for(int l = 1; l <= 8-col; l++){ // diagonal down right
                if(row - l > 0){
                    temp = new ChessPosition(row - l, col + l);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else {
                        if ((board.getPiece(temp)).getTeamColor() == opponent){
                            possibleMoves.add(new ChessMove(position, temp, null));
                        }
                        break;
                    }
                }
            }
        }
        if(type == KING){
            if(row < boardLength){ // move forward
                temp = new ChessPosition(row + 1, col);
                if(board.getPiece(temp) == null){
                    possibleMoves.add(new ChessMove(position, temp, null));
                } else {
                    if ((board.getPiece(temp)).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
                if(col < 8){ // move diagonal up right
                    temp = new ChessPosition(row + 1, col + 1);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else {
                        if ((board.getPiece(temp)).getTeamColor() == opponent){
                            possibleMoves.add(new ChessMove(position, temp, null));
                        }
                    }
                }
                if(col > 1){ // move diagonal up left
                    temp = new ChessPosition(row + 1, col - 1);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else {
                        if ((board.getPiece(temp)).getTeamColor() == opponent){
                            possibleMoves.add(new ChessMove(position, temp, null));
                        }
                    }
                }
            }
            if(row > 1){ // move down
                temp = new ChessPosition(row - 1, col);
                if(board.getPiece(temp) == null){
                    possibleMoves.add(new ChessMove(position, temp, null));
                } else {
                    if ((board.getPiece(temp)).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
                if(col < 8){ // move diagonal down right
                    temp = new ChessPosition(row - 1, col + 1);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else {
                        if ((board.getPiece(temp)).getTeamColor() == opponent){
                            possibleMoves.add(new ChessMove(position, temp, null));
                        }
                    }
                }
                if(col > 1){ // move diagonal down left
                    temp = new ChessPosition(row - 1, col - 1);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else {
                        if ((board.getPiece(temp)).getTeamColor() == opponent){
                            possibleMoves.add(new ChessMove(position, temp, null));
                        }
                    }
                }
            }
            if(col < 8){ // move straight right
                temp = new ChessPosition(row, col + 1);
                if(board.getPiece(temp) == null){
                    possibleMoves.add(new ChessMove(position, temp, null));
                } else {
                    if ((board.getPiece(temp)).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
            }
            if(col > 1){ // move straight left
                temp = new ChessPosition(row, col - 1);
                if(board.getPiece(temp) == null){
                    possibleMoves.add(new ChessMove(position, temp, null));
                } else {
                    if ((board.getPiece(temp)).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
            }
        }
        return possibleMoves;
    }
}
 //make helper function that checks if null or enemy there and adds chess position