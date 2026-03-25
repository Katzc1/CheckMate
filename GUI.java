import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class GUI extends JFrame {
    private JButton[][] squares;
    private NetworkManager nm;
    private ChessBoard board;
    private boolean myTurn;
    private int startRow = -1, startCol = -1;
    private String color;
    private boolean isBot; 
    private Map<String, ImageIcon> imageCache = new HashMap<>();

    public GUI(ChessBoard board, NetworkManager nm, boolean isWhite, boolean isBot) {
        this.board = board;
        this.nm = nm;
        this.isBot = isBot; 
        this.myTurn = isWhite;
        this.color = isWhite ? "White" : "Black";
        
        preloadAllImages();
        squares = new JButton[8][8];
        setTitle("CheckMate - " + this.color);
        setSize(600, 600);
        setLayout(new GridLayout(8, 8));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        drawBoard();
        updateSquareVisuals();
        
        // Network listener starts only if not a local bot game
        if(!isBot && nm != null) startListening();
        
        setVisible(true);
    }

    private void preloadAllImages() {
        String[] colors = {"black", "white"};
        String[] pieces = {"Pawn", "Rook", "Knight", "Bishop", "Queen", "King"};
        for (String c : colors) {
            for (String p : pieces) {
                String fn = c + p + ".png";
                try {
                    Image img = ImageIO.read(new File(fn));
                    // Standardizing size to 65x65
                    imageCache.put(fn, new ImageIcon(img.getScaledInstance(65, 65, Image.SCALE_SMOOTH)));
                } catch (Exception e) {
                    System.out.println("Could not load image: " + fn);
                }
            }
        }
    }

    private void drawBoard() {
        for(int r = 0; r < 8; r++) {
            for(int f = 0; f < 8; f++) {
                JButton s = new JButton();
                s.setBackground((r + f) % 2 == 0 ? Color.WHITE : Color.DARK_GRAY);
                s.setOpaque(true); 
                s.setContentAreaFilled(true);
                s.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                
                final int fr = r, ff = f;
                s.addActionListener(e -> handleSquareClick(fr, ff));
                squares[r][f] = s; 
                add(s);
            }
        }
    }

    private void handleSquareClick(int r, int f) {
        if (!myTurn) return;

        if (startRow == -1) {
            Square s = ChessBoard.getSquare(r, f);
            // Only select if it's the player's own piece
            if (s.getOccupancy() && s.getOccupant().getColor().equalsIgnoreCase(this.color)) {
                startRow = r; startCol = f;
                squares[r][f].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
            }
        } else {
            Square startSq = ChessBoard.getSquare(startRow, startCol);
            Move m = new Move(startSq, ChessBoard.getSquare(r, f), color);
            
            if (m.checkValidMove()) {
                // Check for Pawn Promotion
                if (startSq.getOccupant() instanceof Pawn && (r == 0 || r == 7)) {
                    m.promotionType = promptPromotion();
                }
                
                myTurn = false; 
                if(!isBot && nm != null) nm.sendMove(m);
                
                applyMoveLocally(m);
                
                if(isBot) {
                    Timer t = new Timer(700, e -> triggerBotMove());
                    t.setRepeats(false);
                    t.start();
                }
            }
            // Reset visual border
            squares[startRow][startCol].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            startRow = -1; startCol = -1;
        }
    }

    private int promptPromotion() {
        String[] opts = {"Queen", "Rook", "Bishop", "Knight"};
        int res = JOptionPane.showOptionDialog(this, "Promote Pawn to:", "Promotion", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
        // Returns 1 for Queen, 2 for Rook, etc. based on your promotion logic
        return (res == -1) ? 1 : res + 1;
    }

    private void applyMoveLocally(Move m) {
        m.executeMove();
        updateSquareVisuals();
    }

    private void triggerBotMove() {
        ChessBot bot = new ChessBot();
        String botCol = color.equalsIgnoreCase("White") ? "Black" : "White";
        Move bm = bot.generateRandomMove(this.board, botCol);
        
        if (bm != null) {
            // Bot default promotion to Queen
            if (ChessBoard.getSquare(bm.startRow, bm.startCol).getOccupant() instanceof Pawn && (bm.endRow == 0 || bm.endRow == 7)) {
                bm.promotionType = 1;
            }
            applyMoveLocally(bm);
        }
        myTurn = true; 
    }

    private void updateSquareVisuals() {
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                Square s = ChessBoard.getSquare(r, f);
                if (s.getOccupancy()) {
                    Piece p = s.getPieceOnSquare();
                    // Assumes filename convention like "whitePawn.png"
                    String fn = p.getColor().toLowerCase() + p.getClass().getSimpleName() + ".png";
                    squares[r][f].setIcon(imageCache.get(fn));
                } else {
                    squares[r][f].setIcon(null);
                }
            }
        }
        revalidate(); 
        repaint();
    }

    private void startListening() {
        new Thread(() -> {
            try { 
                while (true) {
                    Move in = nm.receiveMove();
                    if (in != null) {
                        SwingUtilities.invokeLater(() -> { 
                            applyMoveLocally(in); 
                            myTurn = true; 
                        });
                    }
                }
            } catch (Exception e) {
                System.out.println("Network connection closed.");
            }
        }).start();
    }

    public void checkmatePopup(String winnerColor) {
        JOptionPane.showMessageDialog(this, "GAME OVER! " + winnerColor + " Won!", "Checkmate", JOptionPane.INFORMATION_MESSAGE);
        this.setVisible(false); 
        this.dispose();  
        System.exit(0);         
    }
}