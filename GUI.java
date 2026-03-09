import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
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
                
                //find color
                Color squareColor = (row + col) % 2 == 0 ? Color.WHITE : Color.DARK_GRAY;
                square.setBackground(squareColor);

                //Need these lines to show color properly on mac 
                square.setOpaque(true);
                square.setBorderPainted(false);

                //clicks
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
            // First click: Select a piece
            startRow = row;
            startCol = col;
            squares[row][col].setBorderPainted(true);
            squares[row][col].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        } else {
            // Second click: Execute move
            Move move = new Move(startRow, startCol, row, col);
            
            // 1. Send to the other player
            nm.sendMove(move); 
            
            // 2. Update your own screen
            applyMoveLocally(move); 
            
            // 3. END YOUR TURN
            myTurn = false; 
            
            // Reset selection highlights
            squares[startRow][startCol].setBorder(null);
            squares[startRow][startCol].setBorderPainted(false);
            startRow = -1; startCol = -1;
        }
    }
    
    
    private void startListening() {
        new Thread(() -> {
            try {
                while (true) {
                    Move incoming = nm.receiveMove();
                    // Use SwingUtilities to ensure UI updates happen on the correct thread
                    SwingUtilities.invokeLater(() -> {
                        applyMoveLocally(incoming);
                        myTurn = true; // IT IS NOW YOUR TURN
                    });
                }
            } catch (Exception e) { 
                System.out.println("Disconnected: " + e.getMessage()); 
            }
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