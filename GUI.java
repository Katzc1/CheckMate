import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class GUI extends JFrame {
    private ChessBoard board;
    private JButton[][] squares;
    private NetworkManager nm;
    private boolean myTurn;
    private int startRow = -1, startCol = -1;
    private String color;
    private boolean isBot; 

    // Cache to store images in memory so we don't reload from disk every move
    private Map<String, ImageIcon> imageCache = new HashMap<>();

    public GUI(ChessBoard board, NetworkManager nm, boolean isWhite, boolean isBot) {
        this.board = board;
        this.nm = nm;
        this.isBot = isBot; 
        this.myTurn = isWhite;
        this.color = isWhite ? "White" : "Black";
        
        // Load images into RAM once at startup
        preloadAllImages();

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

    private void preloadAllImages() {
        String[] colors = {"black", "white"};
        String[] pieces = {"Pawn", "Rook", "Knight", "Bishop", "Queen", "King"};
        for (String c : colors) {
            for (String p : pieces) {
                // This matches your specific filename format: blackBishop.png, whitePawn.png, etc.
                String fileName = c + p + ".png";
                imageCache.put(fileName, loadAndScaleImage(fileName));
            }
        }
    }

    private void drawBoard() {
        for(int rank = 0; rank < 8; rank++) {
            for(int file = 0; file < 8; file++) {
                JButton square = new JButton();
                Color squareColor = (rank + file) % 2 == 0 ? Color.WHITE : Color.DARK_GRAY;
                
                square.setBackground(squareColor);
                square.setOpaque(true); 
                square.setContentAreaFilled(true);
                square.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                
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
            }
            
            squares[startRow][startCol].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
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
                        myTurn = true;
                    });
                }
            } catch (Exception e) { 
                System.out.println("Disconnected: " + e.getMessage()); 
            }
        }).start();
    }

    private void applyMoveLocally(Move m) {
        m.executeMove();
        updateSquareVisuals();
    }
    
    private void updateSquareVisuals() {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Square sq = ChessBoard.getSquare(rank, file);
                
                if (sq.getOccupancy()) {
                    Piece p = sq.getPieceOnSquare();
                    String fileName = getFileNameForPiece(p);
                    squares[rank][file].setIcon(imageCache.get(fileName));
                } else {
                    squares[rank][file].setIcon(null);
                }
                squares[rank][file].setText("");
            }
        }
        this.revalidate();
        this.repaint();
    }

    private String getFileNameForPiece(Piece p) {
        // Standardizes to "blackPawn.png" or "whitePawn.png"
        String pieceType = p.getClass().getSimpleName();
        String pieceColor = p.getColor().substring(0, 1).toLowerCase() + p.getColor().substring(1).toLowerCase();
        return pieceColor + pieceType + ".png";
    }

    private ImageIcon loadAndScaleImage(String path) {
        try {
            File imgFile = new File(path);
            if (!imgFile.exists()) return null; 

            BufferedImage img = ImageIO.read(imgFile);
            // Scaled to 65x65 for a 600x600 window
            Image scaled = img.getScaledInstance(65, 65, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return null;
        }
    } 
}