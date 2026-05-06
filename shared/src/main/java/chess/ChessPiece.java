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

    ChessGame.TeamColor Color;
    ChessPiece.PieceType Type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        Color = pieceColor;
        Type = type;
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
        return this.Color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.Type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return getPieceMoves(Color, Type, myPosition, board);
    }

    @Override
    public String toString() {
        if(Color == WHITE){ // uppercase
            if(Type == PAWN) return "P";
            if(Type == ROOK) return "R";
            if(Type == QUEEN) return "Q";
            if(Type == KNIGHT) return "N";
            if(Type == KING) return "K";
            if(Type == BISHOP) return "B";
        }
        //lowercase
        if(Type == PAWN) return "p";
        if(Type == ROOK) return "r";
        if(Type == QUEEN) return "q";
        if(Type == KNIGHT) return "n";
        if(Type == KING) return "k";
        if(Type == BISHOP) return "b";
        return " ";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return Color == that.Color && Type == that.Type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Color, Type);
    }
}
