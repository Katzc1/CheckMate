import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    private ChessBoard board;
    private JButton[][] squares;
    private NetworkManager nm;
    private boolean myTurn;
    private int startRow = -1, startCol = -1;

    public GUI(ChessBoard board, NetworkManager nm, boolean isWhite) {
        this.board = board;
        this.nm = nm;
        this.myTurn = isWhite;
        
        squares = new JButton[8][8];
        setTitle("CheckMate - " + (isWhite ? "White" : "Black"));
        setSize(600, 600);
        setLayout(new GridLayout(8, 8));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        drawBoard();
        startListening();
        setVisible(true);
    }

    private void drawBoard() {
        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 8; col++) {
                JButton square = new JButton();
                square.setBackground((row + col) % 2 == 0 ? Color.WHITE : Color.GRAY);
                final int r = row;
                final int c = col;
                square.addActionListener(e -> handleSquareClick(r, c));
                squares[row][col] = square;
                add(square);
            }
        }
    }

    private void handleSquareClick(int row, int col) {
        if (!myTurn) return;
        if (startRow == -1) {
            startRow = row;
            startCol = col;
            squares[row][col].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        } else {
            Move move = new Move(startRow, startCol, row, col);
            nm.sendMove(move);
            applyMoveLocally(move);
            myTurn = false;
            squares[startRow][startCol].setBorder(null);
            startRow = -1; startCol = -1;
        }
    }

    private void startListening() {
        new Thread(() -> {
            try {
                while (true) {
                    Move incoming = nm.receiveMove();
                    applyMoveLocally(incoming);
                    myTurn = true;
                }
            } catch (Exception e) { System.out.println("Disconnected."); }
        }).start();
    }

    private void applyMoveLocally(Move m) {
        // [cite: 1] - Fixed: Updating the logic board as well as the UI
        Square start = board.getSquare(m.startRow, m.startCol);
        Square end = board.getSquare(m.endRow, m.endCol);
        
        if (start.isOccupied) {
            Piece p = start.getPieceOnSquare();
            start.unOccupySquare();
            if (end.isOccupied) end.unOccupySquare();
            end.occupySquare(p);
            p.setLocation(end);
        }

        squares[m.endRow][m.endCol].setText(squares[m.startRow][m.startCol].getText());
        squares[m.startRow][m.startCol].setText("");
        repaint();
    }
}