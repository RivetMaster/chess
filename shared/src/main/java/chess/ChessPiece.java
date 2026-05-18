package chess;

import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;
import static chess.ChessPieceMoves.getPieceMoves;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor color; //team color of the piece
    private final ChessPiece.PieceType type; //type of chess piece

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return getPieceMoves(color, type, myPosition, board);
    }

    @Override
    public String toString() {
        //maybe find data construct like dictionary, make and just return co-value, use makeUpper or something like that if white
        if(color == WHITE){ // uppercase
            if(type == PAWN) {
                return " P  ";
            }
            if(type == ROOK) {
                return " R  ";
            }
            if(type == QUEEN) {
                return " Q  ";
            }
            if(type == KNIGHT) {
                return " N  ";
            }
            if(type == KING) {
                return " K  ";
            }
            if(type == BISHOP) {
                return " B  ";
            }
        }
        //lowercase
        if(type == PAWN) {
            return " p  ";
        }
        if(type == ROOK) {
            return " r  ";
        }
        if(type == QUEEN) {
            return " q  ";
        }
        if(type == KNIGHT) {
            return " n  ";
        }
        if(type == KING) {
            return " k  ";
        }
        if(type == BISHOP) {
            return " b  ";
        }
        return " ";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return getTeamColor() == that.getTeamColor() && getPieceType() == that.getPieceType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamColor(), getPieceType());
    }
}
