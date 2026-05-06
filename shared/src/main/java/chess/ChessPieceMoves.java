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

    //King
    //
    //Kings may move 1 square in any direction (including diagonal) to either a position occupied by an enemy piece (capturing the enemy piece),
    // or to an unoccupied position. A player is not allowed to make any move that would allow the opponent to capture their King. If your King is
    // in danger of being captured on your turn, you must make a move that removes your King from immediate danger.
    //Pawn
    //
    //Pawns normally may move forward one square if that square is unoccupied, though if it is the first time that pawn is being moved,
    // it may be moved forward 2 squares (provided both squares are unoccupied). Pawns cannot capture forward, but instead capture forward diagonally
    // (1 square forward and 1 square sideways). They may only move diagonally like this if capturing an enemy piece.
    // When a pawn moves to the end of the board (row 8 for white and row 1 for black), they get promoted and are replaced with the player's choice of Rook,
    // Knight, Bishop, or Queen (they cannot stay a Pawn or become King).
    //Rook
    //
    //Rooks may move in straight lines as far as there is open space. If there is an enemy piece at the end of the line, rooks may move to that position
    // and capture the enemy piece.
    //Knight
    //
    //Knights move in an L shape, moving 2 squares in one direction and 1 square in the other direction. Knights are the only piece that can ignore pieces
    // in the in-between squares (they can "jump" over other pieces). They can move to squares occupied by an enemy piece and capture the enemy piece, or to
    // unoccupied squares.
    //Bishop
    //
    //Bishops move in diagonal lines as far as there is open space. If there is an enemy piece at the end of the diagonal, the bishop may move to that
    // position and capture the enemy piece.
    //Queen
    //
    //Queens are the most powerful piece and may move in straight lines and diagonals as far as there is open space. If there is an enemy piece at the
    // end of the line, they may move to that position and capture the enemy piece. (In simpler terms, Queens can take all moves a Rook or Bishop could
    // take from the Queen's position).

    static public Collection<ChessMove> getPieceMoves(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type, ChessPosition position, ChessBoard board){
        Collection<ChessMove> possibleMoves;
        possibleMoves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        ChessPosition temp;
        ChessGame.TeamColor opponent = WHITE;
        if(pieceColor == WHITE) {
            opponent = BLACK;
        }
        //Rook moves
        if(type==PAWN){
            //if white pawn
            if(pieceColor == WHITE){
                //move 1 forward with no promotion
                if(row < 7) {
                    temp = new ChessPosition(row + 1, col);
                    if (board.getPiece(temp) == null) {
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                } else if (row == 7){
                    //move 1 forward with all options for promotion
                    temp = new ChessPosition(row+1, col);
                    if (board.getPiece(temp) == null) {
                        for(ChessPiece.PieceType t : promotionTypes){
                            possibleMoves.add(new ChessMove(position, temp, t));
                        }
                    }
                }
                if(row == 2) { //only time pawn still at row 2 is when can move forward 2 or 1
                    temp = new ChessPosition(row + 2, col);
                    if (board.getPiece(temp) == null && board.getPiece(new ChessPosition(row + 1, col)) == null) {
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
                // ADD MOVE DIAGONAL TO CAPTURE
                if(col > 1){
                    //check left diagonal
                    temp = new ChessPosition(row + 1, col - 1);
                    if(board.getPiece(temp) != null && (board.getPiece(temp)).getTeamColor() == BLACK){
                        if(row == 7){
                            for(ChessPiece.PieceType t : promotionTypes){
                                possibleMoves.add(new ChessMove(position, temp, t));
                            }
                        } else{
                            possibleMoves.add(new ChessMove(position, temp, null));
                        }
                    }
                }
                if(col < 8){
                    //check right diagonal
                    temp = new ChessPosition(row + 1, col + 1);
                    if(board.getPiece(temp) != null && (board.getPiece(temp)).getTeamColor() == BLACK){
                        if(row == 7){
                            for(ChessPiece.PieceType t : promotionTypes){
                                possibleMoves.add(new ChessMove(position, temp, t));
                            }
                        } else{
                            possibleMoves.add(new ChessMove(position, temp, null));
                        }
                    }
                }

            //if black pawn
            } else if(pieceColor == BLACK){
                if(row > 2){
                    temp = new ChessPosition(row-1, col);
                    if (board.getPiece(temp) == null) {
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                } else if (row == 2){
                    //move 1 forward with all options for promotion
                    temp = new ChessPosition(row-1, col);
                    if (board.getPiece(temp) == null) {
                        for (ChessPiece.PieceType t : promotionTypes) {
                            possibleMoves.add(new ChessMove(position, temp, t));
                        }
                    }
                }
                if(row == 7) { //only time pawn still at row 7 is when can move forward 2 or 1
                    temp = new ChessPosition(row - 2, col);
                    if (board.getPiece(temp) == null && board.getPiece(new ChessPosition(row-1, col)) == null) {
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
                //ADD MOVE DIAGONAL TO CAPTURE
                if(col > 1){
                    //check left diagonal
                    temp = new ChessPosition(row - 1, col - 1);
                    if(board.getPiece(temp) != null && (board.getPiece(temp)).getTeamColor() == WHITE){
                        if(row == 2){
                            for(ChessPiece.PieceType t : promotionTypes){
                                possibleMoves.add(new ChessMove(position, temp, t));
                            }
                        } else{
                            possibleMoves.add(new ChessMove(position, temp, null));
                        }
                    }
                }
                if(col < 8){
                    //check right diagonal
                    temp = new ChessPosition(row - 1, col + 1);
                    if(board.getPiece(temp) != null && (board.getPiece(temp)).getTeamColor() == WHITE){
                        if(row == 2){
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
            //search up vertically, adding spots to valid moves up to and including one spot that is occupied, but not past
            for(int r = row+1; r<9; r++){
                temp = new ChessPosition(r, col);
                if(board.getPiece(temp) == null){
                    possibleMoves.add(new ChessMove(position, temp, null));
                } else {
                    if ((board.getPiece(temp)).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                    break;
                }
            }
            //search down vertically, adding spots to valid moves up to and including one spot that is occupied, but not past
            for(int r = row-1; r>0; r--){
                temp = new ChessPosition(r, col);
                if(board.getPiece(temp) == null){
                    possibleMoves.add(new ChessMove(position, temp, null));
                } else{
                    if ((board.getPiece(temp)).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                    break;
                }
            }
            //search right, adding spots to valid moves up to and including one spot that is occupied, but not past
            for(int c = col+1; c<9; c++){
                temp = new ChessPosition(row, c);
                if(board.getPiece(temp) == null){
                    possibleMoves.add(new ChessMove(position, temp, null));
                } else {
                    if ((board.getPiece(temp)).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                    break;
                }
            }
            //search left, adding spots to valid moves up to and including one spot that is occupied, but not past
            for(int c = col-1; c>0; c--){
                temp = new ChessPosition(row, c);
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
        if(type == KNIGHT){
            //check forward jumps
            if (row < 7){
                if(col < 7) { //forward 2, right 1
                    temp = new ChessPosition(row + 2, col +1);
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
            // check backwards jumps
            if(row > 2){
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
            //check left jumps
            if(col > 2){
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
            //check right jumps
            if(col < 7){
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
            //diagonal to left and up
            for(int l = 1; l < col; l++){
                if(row + l <= 8){
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
            //diagonal to left and down
            for(int l = 1; l < col; l++){
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
            for(int l = 1; l <= 8-col; l++){
                if(row + l < 9){
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
            //diagonal to right and down
            for(int l = 1; l <= 8-col; l++){
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
            if(row < 8){
                temp = new ChessPosition(row + 1, col);
                if(board.getPiece(temp) == null){
                    possibleMoves.add(new ChessMove(position, temp, null));
                } else {
                    if ((board.getPiece(temp)).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
                if(col < 8){
                    temp = new ChessPosition(row + 1, col + 1);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else {
                        if ((board.getPiece(temp)).getTeamColor() == opponent){
                            possibleMoves.add(new ChessMove(position, temp, null));
                        }
                    }
                }
                if(col > 1){
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
            if(row > 1){
                temp = new ChessPosition(row - 1, col);
                if(board.getPiece(temp) == null){
                    possibleMoves.add(new ChessMove(position, temp, null));
                } else {
                    if ((board.getPiece(temp)).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
                if(col < 8){
                    temp = new ChessPosition(row - 1, col + 1);
                    if(board.getPiece(temp) == null){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    } else {
                        if ((board.getPiece(temp)).getTeamColor() == opponent){
                            possibleMoves.add(new ChessMove(position, temp, null));
                        }
                    }
                }
                if(col > 1){
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
            if(col < 8){
                temp = new ChessPosition(row, col + 1);
                if(board.getPiece(temp) == null){
                    possibleMoves.add(new ChessMove(position, temp, null));
                } else {
                    if ((board.getPiece(temp)).getTeamColor() == opponent){
                        possibleMoves.add(new ChessMove(position, temp, null));
                    }
                }
            }
            if(col > 1){
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