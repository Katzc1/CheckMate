
import javax.swing.*;
import java.awt.*;

public class ChessBoard extends JFrame{

    private static Square[][] board;

    public ChessBoard() {

        board = new Square[8][8];

        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 8; col++) {

                board[row][col] = new Square(row, col);

            }
        }
    }

    public static Square getSquare(int row, int col) {
        return board[row][col];
    }

    public static Square[][] getBoard() {
        return board;
    }
}
