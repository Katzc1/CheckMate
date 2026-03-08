
import javax.swing.*;
import java.awt.*;

public class ChessBoard extends JFrame{

    private Square[][] board;

    public ChessBoard() {

        board = new Square[8][8];

        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 8; col++) {

                board[row][col] = new Square(row, col);

            }
        }
    }

    public Square getSquare(int row, int col) {
        return board[row][col];
    }

    public Square[][] getBoard() {
        return board;
    }
}
