import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

@SuppressWarnings("serial")
public class GUI extends JFrame {
    private ChessBoard board;
    private JButton[][] squares;
    private NetworkManager nm;
    private boolean myTurn;
    private int startRow = -1, startCol = -1;
    private String color;
    private boolean isBot; 

    public GUI(ChessBoard board, NetworkManager nm, boolean isWhite, boolean isBot) {
        this.board = board;
        this.nm = nm;
        this.isBot = isBot; 
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
        updateSquareVisuals();
        
        if(!isBot) {
            startListening();
        }
        
        setVisible(true);
    }

    private void drawBoard() {
        for(int rank = 0; rank < 8; rank++) {
            for(int file = 0; file < 8; file++) {
                JButton square = new JButton();
                
                // Logic check: (Rank + File) % 2 determines color
                Color squareColor = (rank + file) % 2 == 0 ? Color.WHITE : Color.DARK_GRAY;
                
                square.setBackground(squareColor);
                
                // --- THE FIX FOR THE DISAPPEARING BACKGROUND ---
                square.setOpaque(true); 
                square.setContentAreaFilled(true); // Must be true to see the background color
                square.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Optional: adds a thin grid line
                
                final int r = rank;
                final int f = file;
                square.addActionListener(e -> handleSquareClick(r, f));
                
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
                // CRITICAL: The checkValidMove() above sets thisMove.isEnPassant to true 
                // if it's a valid capture. Now we send that 'true' flag to the friend.
                if(!isBot) {
                    nm.sendMove(thisMove); 
                }
                
                applyMoveLocally(thisMove); 
                myTurn = false;

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

        updateSquareVisuals();
     
        revalidate();
        repaint();
    }
    
    private void updateSquareVisuals() {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
            	//using  ChessBoard.getSquare to be safer
                Square sq = ChessBoard.getSquare(rank, file);
                
                if (sq.getOccupancy()) {// make sure this matches Square.java
                    Piece p = sq.getPieceOnSquare();
                    String fileName = getFileNameForPiece(p); // Extracted to a helper below
                    
                    ImageIcon icon = loadAndScaleImage(fileName);
                    squares[rank][file].setIcon(icon);
                    squares[rank][file].setText("");//Clear text so Icon is visible
                } else {
                    squares[rank][file].setIcon(null);
                    squares[rank][file].setText("");
                }
            }
        }
        // Force the UI to refresh so changes appear immediately
        this.revalidate();
        this.repaint();
    }

    private String getFileNameForPiece(Piece p) {
        if (p.getColor().equalsIgnoreCase("Black")) {
            if (p instanceof Bishop) return "blackBishop.png";
            if (p instanceof Knight) return "blackKnight.png";
            if (p instanceof King)   return "blackKing.png";
            if (p instanceof Queen)  return "blackQueen.png";
            if (p instanceof Rook)   return "blackRook.png";
            if (p instanceof Pawn)   return "blackPawn.png";
        }
        return p.getColor() + p.getClass().getSimpleName() + ".png";
    }
    private ImageIcon loadAndScaleImage(String path) {
        try {
            File imgFile = new File(path);
            
            if (!imgFile.exists()) {
                // This will tell you EXACTLY where Java is looking
                System.out.println("DEBUG: Cannot find file at: " + imgFile.getAbsolutePath());
                return null; 
            }

            BufferedImage img = ImageIO.read(imgFile);
            Image scaled = img.getScaledInstance(300, 400, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            System.out.println("DEBUG: Error reading file: " + path);
            e.printStackTrace();
            return null;
        }
    } 
}