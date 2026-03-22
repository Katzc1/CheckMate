import javax.swing.*;
import java.awt.*;


@SuppressWarnings("serial")
public class GUI extends JFrame {
    private ChessBoard board;
    private JButton[][] squares;
    private NetworkManager nm;
    private boolean myTurn;
    private int startRow = -1, startCol = -1;
    private String color;

    public GUI(ChessBoard board, NetworkManager nm, boolean isWhite) {
        this.board = board;
        this.nm = nm;
        this.myTurn = isWhite;
        if(isWhite) {
        	this.color = "White";
        } else {
        	this.color = "Black";
        }
        
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
        for(Square[] r: ChessBoard.getBoard()) {
        	for(Square s: r) {
        		
        		final int rank = s.getRank();
                final int file = s.getFile();
                
                JButton square = new JButton();
                
                if(s.getOccupancy()) {
                	square = new JButton(s.getOccupant().getClass().getSimpleName());
                	if(s.getOccupant().getColor() == "White") {
                		square.setForeground(Color.BLUE);
                	} else {
                		square.setForeground(Color.RED);
                	}
                } else {
                	square = new JButton();
                }
                
                
                //find color
                Color squareColor = (rank + file) % 2 == 0 ? Color.WHITE : Color.DARK_GRAY;
                square.setBackground(squareColor);
                

                //Need these lines to show color properly on mac 
                square.setOpaque(true);
                square.setBorderPainted(false);

                //clicks
                square.addActionListener(e -> handleSquareClick(rank, file));
                
                squares[rank][file] = square;
                add(square);
        	}
        }
    }

    private void handleSquareClick(int rank, int file) {
        if (!myTurn) return; 

        if (startRow == -1) {
            startRow = rank;
            startCol = file;
            squares[rank][file].setBorderPainted(true);
            squares[rank][file].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        } else {
            Move thisMove = new Move(ChessBoard.getSquare(startRow, startCol), ChessBoard.getSquare(rank, file), color);
            
            if (thisMove.checkValidMove()) {
                nm.sendMove(thisMove); 
                applyMoveLocally(thisMove); 
                myTurn = false; // Turn ends ONLY if move was valid
            } else {
                System.out.println("Invalid move attempted.");
            }
            
            // Always clear selection
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
                    SwingUtilities.invokeLater(() -> {
                        applyMoveLocally(incoming);
                        GameStateManager.tickMoveTimer();
                        myTurn = true; // IT IS NOW YOUR TURN
                    });
                }
            } catch (Exception e) { 
                System.out.println("Disconnected: " + e.getMessage()); 
            }
        }).start();
    }

    private void applyMoveLocally(Move m) {
        // Execute the logic
        m.executeMove();

        // Sync the entire UI with the Board Data
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Square s = ChessBoard.getSquare(r, c);
                if (s.getOccupancy()) {
                    squares[r][c].setText(s.getOccupant().getClass().getSimpleName());
                    if (s.getOccupant().getColor().equalsIgnoreCase("White")) {
                        squares[r][c].setForeground(Color.BLUE);
                    } else {
                        squares[r][c].setForeground(Color.RED);
                    }
                } else {
                    squares[r][c].setText("");
                }
            }
        }

        revalidate();
        repaint();
    }
    
    private void updateSquareVisuals() {
        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                Square sq = ChessBoard.getSquare(r, c);
                if (sq.isOccupied) {
                    Piece p = sq.getPieceOnSquare();
                    // Assumes images are named like "WhitePawn.png"
                    String imgName = p.getColor() + p.getClass().getSimpleName() + ".png";
                    squares[r][c].setIcon(new ImageIcon("res/" + imgName));
                } else {
                    squares[r][c].setIcon(null);
                }
            }
        }
    }
}
