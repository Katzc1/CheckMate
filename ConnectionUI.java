import javax.swing.*;
import java.awt.*;

public class ConnectionUI extends JFrame {
    private NetworkManager nm;
    // Added a text field so the user can enter the Host's IP address
    private JTextField ipField;

    public ConnectionUI(NetworkManager nm) {
        this.nm = nm;
        setTitle("CheckMate Online");
        setSize(350, 200);
        setLayout(new GridLayout(4, 1)); 
        
        ipField = new JTextField("127.0.0.1"); // Default to local for testing
        JButton hostBtn = new JButton("Host Game (Port 1234)");
        JButton joinBtn = new JButton("Join Friend");

        hostBtn.addActionListener(e -> {
            new Thread(() -> {
                try {
                    nm.startAsHost(1234);
                    startGame(true);
                } catch (Exception ex) { 
                    ex.printStackTrace(); 
                    JOptionPane.showMessageDialog(this, "Port 1234 is busy. Close other instances!");
                }
            }).start();
        });

        joinBtn.addActionListener(e -> {
            new Thread(() -> {
                try {
                    // Use the IP address typed into the text field
                    String targetIP = ipField.getText().trim();
                    nm.startAsGuest(targetIP, 1234);
                    startGame(false);
                } catch (Exception ex) { 
                    ex.printStackTrace(); 
                    JOptionPane.showMessageDialog(this, "Could not connect. Check IP and Firewall!");
                }
            }).start();
        });

        add(new JLabel("Enter Host IP (Join only):", SwingConstants.CENTER));
        add(ipField);
        add(hostBtn);
        add(joinBtn);
        
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startGame(boolean isWhite) {
        SwingUtilities.invokeLater(() -> {
            this.dispose();
            ChessBoard board = new ChessBoard();
            new GUI(board, this.nm, isWhite);
        });
    }
}