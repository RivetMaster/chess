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

    public ChessPieceMoves(/*ChessGame.TeamColor pieceColor, ChessPiece.PieceType type, ChessPosition position */){}

    static public Collection<ChessMove> getPieceMoves(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type, ChessPosition position, ChessBoard board){
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        int sq_depth = 8;
        int black_pwn_row = 7;
        int white_pwn_row = 2;
        ChessPosition temp;
        ChessGame.TeamColor opponent = WHITE;
        if(pieceColor == WHITE) {
            opponent = BLACK;
        }
        if(type==PAWN){
            if(pieceColor == WHITE){
                if(row < black_pwn_row) { //move no promotion
                    temp = new ChessPosition(row + 1, col);
                    if (board.getPiece(temp) == null) {
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                } else if (row == black_pwn_row){ // move with promotion options
                    temp = new ChessPosition(row + 1, col);
                    if (board.getPiece(temp) == null) {
                        for(ChessPiece.PieceType t : promotionTypes){
                            possibleMoves.add(new ChessMove(position, temp, t));
                        }
                    }
                }
                if(row == white_pwn_row) { //move 2 forward
                    temp = new ChessPosition(row + 2, col);
                    if (board.getPiece(temp) == null && board.getPiece(new ChessPosition(row + 1, col)) == null) {
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
                if(col > 1){ // diagonal capture left
                    temp = new ChessPosition(row + 1, col - 1);
                    if(board.getPiece(temp) != null && (board.getPiece(temp)).getTeamColor() == opponent){
                        if(row == black_pwn_row){ //diagonal promotion
                            for(ChessPiece.PieceType t : promotionTypes){
                                possibleMoves.add(new ChessMove(position, temp, t));
                            }
                        } else{
                            possibleMoves.add(new ChessMove(position, temp, null));
                        }
                    }
                }
                if(col < sq_depth){ //diagonal capture right
                    temp = new ChessPosition(row + 1, col + 1);
                    if(board.getPiece(temp) != null && (board.getPiece(temp)).getTeamColor() == opponent){
                        if(row == black_pwn_row){ //diagonal promotion
                            for(ChessPiece.PieceType t : promotionTypes){
                                possibleMoves.add(new ChessMove(position, temp, t));
                            }
                        } else{
                            possibleMoves.add(new ChessMove(position, temp, null));
                        }
                    }
                }
            } else if(pieceColor == BLACK){
                if(row > white_pwn_row){ //move forward
                    temp = new ChessPosition(row-1, col);
                    if (board.getPiece(temp) == null) {
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                } else if (row == white_pwn_row){ // move forward promotion
                    temp = new ChessPosition(row-1, col);
                    if (board.getPiece(temp) == null) {
                        for (ChessPiece.PieceType t : promotionTypes) {
                            possibleMoves.add(new ChessMove(position, temp, t));
                        }
                    }
                }
                if(row == black_pwn_row) { // move forward 2
                    temp = new ChessPosition(row - 2, col);
                    if (board.getPiece(temp) == null && board.getPiece(new ChessPosition(row-1, col)) == null) {
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
                if(col > 1){ // capture diagonal left
                    temp = new ChessPosition(row - 1, col - 1);
                    if(board.getPiece(temp) != null && (board.getPiece(temp)).getTeamColor() == opponent){
                        if(row == white_pwn_row){
                            for(ChessPiece.PieceType t : promotionTypes){
                                possibleMoves.add(new ChessMove(position, temp, t));
                            }
                        } else{
                            possibleMoves.add(new ChessMove(position, temp, null));
                        }
                    }
                }
                if(col < sq_depth){ // capture diagonal right
                    temp = new ChessPosition(row - 1, col + 1);
                    if(board.getPiece(temp) != null && (board.getPiece(temp)).getTeamColor() == opponent){
                        if(row == white_pwn_row){
                            for(ChessPiece.PieceType t : promotionTypes){
                                possibleMoves.add(new ChessMove(position, temp, t));
                            }
                        } else{
                            possibleMoves.add(new ChessMove(position, temp, null));
                        }
                    }
                }
            }
        }
        if(type == ROOK || type == QUEEN){
            for(int r = row + 1; r <= sq_depth; r++){ //check every square vertically up from position
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
            for(int c = col + 1; c <= sq_depth; c++){ //check every square horizontally right from position
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
            if (row < black_pwn_row){ // check forward jumps
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
                if(row + l <= sq_depth){
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
            for(int l = 1; l <= sq_depth - col; l++){ // diagonal up right
                if(row + l <= sq_depth){
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
            if(row < sq_depth){ // move forward
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