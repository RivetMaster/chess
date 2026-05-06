package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * Refers to position on the board, do minus one for accessing array
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    int Row;
    int Col;

    public ChessPosition(int row, int col) {
        Row = row;
        Col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.Row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return this.Col;
    }

    @Override
    public String toString() {
        return "[" + Row + ", " + Col +"]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return getRow() == that.getRow() && Col == that.Col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), Col);
    }
}
