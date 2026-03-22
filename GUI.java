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
    private boolean isBot; // Added for bot mode

    // Updated constructor with isBot parameter
    public GUI(ChessBoard board, NetworkManager nm, boolean isWhite, boolean isBot) {
        this.board = board;
        this.nm = nm;
        this.isBot = isBot; // Set bot mode
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
        // Only listen for network moves if we are NOT in bot mode
        if(!isBot) {
            startListening();
        }
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
                	if(s.getOccupant().getColor().equals("White")) {
                		square.setForeground(Color.BLUE);
                	} else {
                		square.setForeground(Color.RED);
                	}
                }
                Color squareColor = (rank + file) % 2 == 0 ? Color.WHITE : Color.DARK_GRAY;
                square.setBackground(squareColor);
                square.setOpaque(true);
                square.setBorderPainted(false);
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
                //Only use network manager if not playing a bot
                if(!isBot) {
                    nm.sendMove(thisMove); 
                }
                
                applyMoveLocally(thisMove); 
                myTurn = false;

                //Trigger bot move if in bot mode
                if(isBot) {
                    Timer timer = new Timer(500, e -> triggerBotMove());
                    timer.setRepeats(false);
                    timer.start();
                }
            } else {
                System.out.println("Invalid move attempted.");
            }
            
            squares[startRow][startCol].setBorder(null);
            squares[startRow][startCol].setBorderPainted(false);
            startRow = -1; startCol = -1;
        }
    }
    
    // NEW: Small method to handle the bot's turn
    private void triggerBotMove() {
        ChessBot bot = new ChessBot();
        String botColor = color.equals("White") ? "Black" : "White";
        Move botMove = bot.generateRandomMove(this.board, botColor);

        if (botMove != null) {
            applyMoveLocally(botMove);
            myTurn = true;
        }
    }
    
    private void startListening() {
        new Thread(() -> {
            try {
                while (true) {
                    Move incoming = nm.receiveMove();
                    SwingUtilities.invokeLater(() -> {
                        applyMoveLocally(incoming);
                        myTurn = true; 
                    });
                }
            } catch (Exception e) { 
                System.out.println("Disconnected: " + e.getMessage()); 
            }
        }).start();
    }

    private void applyMoveLocally(Move m) {
        String pieceName = squares[m.getStartRow()][m.getStartCol()].getText();
        m.executeMove();
        squares[m.getEndRow()][m.getEndCol()].setText(pieceName);
        if(ChessBoard.getSquare(m.getEndRow(), m.getEndCol()).getOccupant().getColor().equals("White")) {
        	squares[m.getEndRow()][m.getEndCol()].setForeground(Color.BLUE);
    	} else {
    		squares[m.getEndRow()][m.getEndCol()].setForeground(Color.RED);
    	}
        squares[m.getStartRow()][m.getStartCol()].setText("");
        revalidate();
        repaint();
    }
    
    private void updateSquareVisuals() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Square sq = ChessBoard.getSquare(r, c);
                if (sq.isOccupied) {
                    Piece p = sq.getPieceOnSquare();
                    String imgName = p.getColor() + p.getClass().getSimpleName() + ".png";
                    squares[r][c].setIcon(new ImageIcon("res/" + imgName));
                } else {
                    squares[r][c].setIcon(null);
                }
            }
        }
    }
}