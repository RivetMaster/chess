package chess;

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
}
