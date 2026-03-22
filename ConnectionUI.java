import javax.swing.*;
import java.awt.*;

public class ConnectionUI extends JFrame {
    private NetworkManager nm;
    private JTextField ipField;

    public ConnectionUI(NetworkManager nm) {
        this.nm = nm;
        setTitle("CheckMate Online");
        setSize(350, 250); 
        setLayout(new GridLayout(5, 1)); 
        
        ipField = new JTextField("127.0.0.1");
        JButton hostBtn = new JButton("Host Game (Port 1234)");
        JButton joinBtn = new JButton("Join Friend");
        
        JButton botBtn = new JButton("Play vs CPU (Offline)");
        botBtn.setBackground(new Color(220, 220, 220)); 

        hostBtn.addActionListener(e -> {
            new Thread(() -> {
                try {
                    nm.startAsHost(1234);
                    startGame(true, false); // isWhite = true, isBot = false
                } catch (Exception ex) { 
                    ex.printStackTrace(); 
                    JOptionPane.showMessageDialog(this, "Port 1234 is busy.");
                }
            }).start();
        });

        joinBtn.addActionListener(e -> {
            new Thread(() -> {
                try {
                    String targetIP = ipField.getText().trim();
                    nm.startAsGuest(targetIP, 1234);
                    startGame(false, false); // isWhite = false, isBot = false
                } catch (Exception ex) { 
                    ex.printStackTrace(); 
                    JOptionPane.showMessageDialog(this, "Could not connect.");
                }
            }).start();
        });

        // --- BOT ACTION ---
        botBtn.addActionListener(e -> {
            startGame(true, true); 
        });

        add(new JLabel("Enter Host IP (Join only):", SwingConstants.CENTER));
        add(ipField);
        add(hostBtn);
        add(joinBtn);
        add(botBtn); // Add the new button to the UI
        
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Added isBot parameter to pass to the GUI
    private void startGame(boolean isWhite, boolean isBot) {
        SwingUtilities.invokeLater(() -> {
            this.dispose();
            ChessBoard board = new ChessBoard();
            new GUI(board, this.nm, isWhite, isBot);
        });
    }
}