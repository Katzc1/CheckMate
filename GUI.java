import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
	
	private ChessBoard board;
	private JButton[][] squares;
	
	public GUI(ChessBoard board) {
		this.board = board;
		squares = new JButton[8][8];
		
		setTitle("CheckMate");
		setSize(600, 600);
		setLayout(new GridLayout(8, 8));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		drawBoard();
		
		setVisible(true);
		
	

	}

	private void drawBoard() {
		Square[][]gameBoard = board.getBoard();
		
		for(int row= 0; row < 8; row++) {
			for(int col = 0; col < 8; col++) {
				
				JButton square = new JButton();
				if ((row + col) % 2 == 0) {
					square.setBackground(Color.WHITE);
				}else {
					square.setBackground(Color.GRAY);
				}
				
				squares[row][col] = square;
				add(square);
			}
		}
	}
}
