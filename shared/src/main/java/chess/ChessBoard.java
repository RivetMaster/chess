package chess;

import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[8][8]; //make new empty board
        //Rooks
        squares[0][0] = new ChessPiece(WHITE, ROOK);
        squares[0][7] = new ChessPiece(WHITE, ROOK);
        squares[7][0] = new ChessPiece(BLACK, ROOK);
        squares[7][7] = new ChessPiece(BLACK, ROOK);
        //Knights
        squares[0][1] = new ChessPiece(WHITE, KNIGHT);
        squares[0][6] = new ChessPiece(WHITE, KNIGHT);
        squares[7][1] = new ChessPiece(BLACK, KNIGHT);
        squares[7][6] = new ChessPiece(BLACK, KNIGHT);
        //Bishops
        squares[0][2] = new ChessPiece(WHITE, BISHOP);
        squares[0][5] = new ChessPiece(WHITE, BISHOP);
        squares[7][2] = new ChessPiece(BLACK, BISHOP);
        squares[7][5] = new ChessPiece(BLACK, BISHOP);
        //Queen
        squares[0][3] = new ChessPiece(WHITE, QUEEN);
        squares[7][3] = new ChessPiece(BLACK, QUEEN);
        //King
        squares[0][4] = new ChessPiece(WHITE, QUEEN);
        squares[7][4] = new ChessPiece(BLACK, QUEEN);
        //Pawns
        for(ChessPiece sq : squares[1]){
            sq = new ChessPiece(WHITE, PAWN);
        }
        for(ChessPiece sq : squares[6]){
            sq = new ChessPiece(BLACK, PAWN);
        }
    }
}

//|r|n|b|q|k|b|n|r|    BLACK row 8
//|p|p|p|p|p|p|p|p|
//| | | | | | | | |
//| | | | | | | | |
//| | | | | | | | |
//| | | | | | | | |
//|P|P|P|P|P|P|P|P|
//|R|N|B|Q|K|B|N|R|    WHITE row 1