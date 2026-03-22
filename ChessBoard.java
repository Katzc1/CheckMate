import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class ChessBoard extends JFrame {

    // Removed 'static' to allow for better object-oriented control
    private static Square[][] board;

    public ChessBoard() {
        board = new Square[8][8];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = new Square(row, col);
            }
        }
        // Initialize the pieces immediately upon board creation
        initPieces();
    }

    private void initPieces() {
        // Setup Pawns
        for (int i = 0; i < 8; i++) {
            // Row 6 for White, Row 1 for Black
            addPieceToSquare(6, i, new Pawn("White", board[6][i]));
            addPieceToSquare(1, i, new Pawn("Black", board[1][i]));
        }

        // Setup Back Rows
        setupBackRow(0, "Black");
        setupBackRow(7, "White");
    }

    private void setupBackRow(int row, String color) {
        addPieceToSquare(row, 0, new Rook(color, board[row][0]));
        addPieceToSquare(row, 1, new Knight(color, board[row][1]));
        addPieceToSquare(row, 2, new Bishop(color, board[row][2]));
        addPieceToSquare(row, 3, new Queen(color, board[row][3]));
        addPieceToSquare(row, 4, new King(color, board[row][4]));
        addPieceToSquare(row, 5, new Bishop(color, board[row][5]));
        addPieceToSquare(row, 6, new Knight(color, board[row][6]));
        addPieceToSquare(row, 7, new Rook(color, board[row][7]));
    }

    private void addPieceToSquare(int row, int col, Piece p) {
        board[row][col].occupySquare(p);
    }

    static public Square getSquare(int row, int col) {
        // Check if the requested row and col are actually on the 0-7 board
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            return board[row][col];
        }
        // Return null if it's off-board so the Knight/Bot knows to ignore it
        return null;
    }

    public static Square[][] getBoard() {
        return board;
    }
    public static boolean squareExists(Square target, int rankDis, int fileDis) {
    	int rank = target.getRank() + rankDis;
    	int file = target.getFile() + fileDis;
    	if(rank > 0 && rank < 9 && file > 0 && file < 9) {
    		return true;
    	}
    	return false;
    }
}