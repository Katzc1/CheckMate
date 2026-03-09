import java.io.Serializable;

public class Move implements Serializable {
    public int startRow, startCol, endRow, endCol;

    public Move(int startRow, int startCol, int endRow, int endCol) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
    }
}