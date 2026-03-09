import java.io.*;
import java.net.*;

public class NetworkManager {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void startAsHost(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        this.socket = serverSocket.accept();
        setupStreams();
        serverSocket.close();
    }

    public void startAsGuest(String ip, int port) throws IOException {
        this.socket = new Socket(ip, port);
        setupStreams();
    }

    private void setupStreams() throws IOException {
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public void sendMove(Move move) {
        try {
            out.writeObject(move);
            out.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public Move receiveMove() throws IOException, ClassNotFoundException {
        return (Move) in.readObject();
    }
}