package chess;

import java.util.Arrays;
import java.util.Objects;

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

    public ChessBoard(ChessBoard copy){
        //makes shallow copy, references to pieces objects, okay because they don't hold position information and making moves makes new pieces
        for(int i = 0; i < 8; i++) {
            this.squares[i] = Arrays.copyOf(copy.squares[i], copy.squares[i].length);
        }
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
        squares[0][4] = new ChessPiece(WHITE, KING);
        squares[7][4] = new ChessPiece(BLACK, KING);
        //Pawns
        for(int sq = 0; sq < 8; sq++) {
            squares[1][sq] = new ChessPiece(WHITE, PAWN);
            squares[6][sq] = new ChessPiece(BLACK, PAWN);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return String.format(
                        "|%s|%s|%s|%s|%s|%s|%s|%s|\n" +
                        "|%s|%s|%s|%s|%s|%s|%s|%s|\n" +
                        "|%s|%s|%s|%s|%s|%s|%s|%s|\n" +
                        "|%s|%s|%s|%s|%s|%s|%s|%s|\n" +
                        "|%s|%s|%s|%s|%s|%s|%s|%s|\n" +
                        "|%s|%s|%s|%s|%s|%s|%s|%s|\n" +
                        "|%s|%s|%s|%s|%s|%s|%s|%s|\n" +
                        "|%s|%s|%s|%s|%s|%s|%s|%s|",
        squares[7][0], squares[7][1], squares[7][2], squares[7][3], squares[7][4], squares[7][5], squares[7][6], squares[7][7],
        squares[6][0], squares[6][1], squares[6][2], squares[6][3], squares[6][4], squares[6][5], squares[6][6], squares[6][7],
        squares[5][0], squares[5][1], squares[5][2], squares[5][3], squares[5][4], squares[5][5], squares[5][6], squares[5][7],
        squares[4][0], squares[4][1], squares[4][2], squares[4][3], squares[4][4], squares[4][5], squares[4][6], squares[4][7],
        squares[3][0], squares[3][1], squares[3][2], squares[3][3], squares[3][4], squares[3][5], squares[3][6], squares[3][7],
        squares[2][0], squares[2][1], squares[2][2], squares[2][3], squares[2][4], squares[2][5], squares[2][6], squares[2][7],
        squares[1][0], squares[1][1], squares[1][2], squares[1][3], squares[1][4], squares[1][5], squares[1][6], squares[1][7],
        squares[0][0], squares[0][1], squares[0][2], squares[0][3], squares[0][4], squares[0][5], squares[0][6], squares[0][7]
                );
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