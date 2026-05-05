package chess;

import java.util.Collection;

/**
 * Calculates the Positions the Chess piece can move to
 */
public class ChessPieceMoves {

    ChessPosition pos;
    ChessPiece.PieceType Type;
    ChessGame.TeamColor Color;

    public ChessPieceMoves(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type, ChessPosition position ){
        pos = position;
        Type = type;
        Color = pieceColor;
    }

    public Collection<ChessMove> getPieceMoves(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type, ChessPosition position){

    }

}
