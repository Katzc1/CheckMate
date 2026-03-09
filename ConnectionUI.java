import javax.swing.*;
import java.awt.*;

public class ConnectionUI extends JFrame {
    private NetworkManager nm;

    public ConnectionUI(NetworkManager nm) {
        this.nm = nm;
        setTitle("CheckMate Online");
        setSize(300, 150);
        setLayout(new GridLayout(3, 1));

        JButton hostBtn = new JButton("Host Game (Port 1234)");
        JButton joinBtn = new JButton("Join Friend (Localhost)");

        hostBtn.addActionListener(e -> {
            new Thread(() -> {
                try {
                    nm.startAsHost(1234);
                    startGame(true);
                } catch (Exception ex) { ex.printStackTrace(); }
            }).start();
        });

        joinBtn.addActionListener(e -> {
            new Thread(() -> {
                try {
                    nm.startAsGuest("127.0.0.1", 1234);
                    startGame(false);
                } catch (Exception ex) { ex.printStackTrace(); }
            }).start();
        });

        add(new JLabel("Choose Connection Mode:", SwingConstants.CENTER));
        add(hostBtn);
        add(joinBtn);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startGame(boolean isWhite) {
        this.dispose();
        ChessBoard board = new ChessBoard();
        }
}