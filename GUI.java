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
    private boolean myTurn;
    private int startRow = -1, startCol = -1;
    private String color;
    private boolean isBot; 
    private Map<String, ImageIcon> imageCache = new HashMap<>();

    public GUI(ChessBoard board, NetworkManager nm, boolean isWhite, boolean isBot) {
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
        if(!isBot) startListening();
        setVisible(true);
    }

    private void preloadAllImages() {
        String[] colors = {"black", "white"}, pieces = {"Pawn", "Rook", "Knight", "Bishop", "Queen", "King"};
        for (String c : colors) for (String p : pieces) {
            String fn = c + p + ".png";
            try {
                Image img = ImageIO.read(new File(fn));
                imageCache.put(fn, new ImageIcon(img.getScaledInstance(65, 65, Image.SCALE_SMOOTH)));
            } catch (Exception e) {}
        }
    }

    private void drawBoard() {
        for(int r = 0; r < 8; r++) for(int f = 0; f < 8; f++) {
            JButton s = new JButton();
            s.setBackground((r + f) % 2 == 0 ? Color.WHITE : Color.DARK_GRAY);
            s.setOpaque(true); s.setContentAreaFilled(true);
            final int fr = r, ff = f;
            s.addActionListener(e -> handleSquareClick(fr, ff));
            squares[r][f] = s; add(s);
        }
    }

    private void handleSquareClick(int r, int f) {
        if (!myTurn) return;
        if (startRow == -1) {
            Square s = ChessBoard.getSquare(r, f);
            if (s.getOccupancy() && s.getOccupant().getColor().equalsIgnoreCase(this.color)) {
                startRow = r; startCol = f;
                squares[r][f].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
            }
        } else {
            Square startSq = ChessBoard.getSquare(startRow, startCol);
            Move m = new Move(startSq, ChessBoard.getSquare(r, f), color);
            
            if (m.checkValidMove()) {
                if (startSq.getOccupant() instanceof Pawn && (r == 0 || r == 7)) m.promotionType = promptPromotion();
                
                myTurn = false; // Turn is over
                if(!isBot && nm != null) nm.sendMove(m);
                applyMoveLocally(m);
                
                if(isBot) {
                    Timer t = new Timer(700, e -> triggerBotMove());
                    t.setRepeats(false);
                    t.start();
                }
            }
            squares[startRow][startCol].setBorder(null);
            startRow = -1; startCol = -1;
        }
    }

    private int promptPromotion() {
        String[] opts = {"Queen", "Rook", "Bishop", "Knight"};
        int res = JOptionPane.showOptionDialog(this, "Promote:", "Promotion", 0, 1, null, opts, opts[0]);
        return (res == -1) ? 1 : res + 1;
    }

    private void applyMoveLocally(Move m) {
        m.executeMove();
        updateSquareVisuals();
    }

    private void triggerBotMove() {
        ChessBot bot = new ChessBot();
        String botCol = color.equalsIgnoreCase("White") ? "Black" : "White";
        Move bm = bot.generateRandomMove(null, botCol);
        
        if (bm != null) {
            System.out.println("Bot is moving: " + bm.startRow + "," + bm.startCol + " to " + bm.endRow + "," + bm.endCol);
            if (ChessBoard.getSquare(bm.startRow, bm.startCol).getOccupant() instanceof Pawn && (bm.endRow == 0 || bm.endRow == 7)) {
                bm.promotionType = 1;
            }
            applyMoveLocally(bm);
        } else {
            System.out.println("Bot failed to find a move!");
        }
        myTurn = true; // HAND TURN BACK
    }

    private void updateSquareVisuals() {
        for (int r = 0; r < 8; r++) for (int f = 0; f < 8; f++) {
            Square s = ChessBoard.getSquare(r, f);
            if (s.getOccupancy()) {
                Piece p = s.getPieceOnSquare();
                String fn = p.getColor().toLowerCase() + p.getClass().getSimpleName() + ".png";
                squares[r][f].setIcon(imageCache.get(fn));
            } else {
                squares[r][f].setIcon(null);
            }
        }
        revalidate(); repaint();
    }

    private void startListening() {
        new Thread(() -> {
            try { while (true) {
                Move in = nm.receiveMove();
                SwingUtilities.invokeLater(() -> { applyMoveLocally(in); myTurn = true; });
            }} catch (Exception e) {}
        }).start();
    }
    public void checkmatePopup(String winnerColor) {
        // Show the pop-up. This execution pauses here until the user clicks "OK"
        JOptionPane.showMessageDialog(this, "GAME OVER! " + winnerColor + " Won!",  "Checkmate",  JOptionPane.INFORMATION_MESSAGE);
        
        // Stop displaying the game
        this.setVisible(false); 
        //Destroy window resources
        this.dispose();  
        //Close the app
        System.exit(0);         
    }

}
