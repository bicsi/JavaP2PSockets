package Common;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketListener extends Thread {
    private final SocketAcceptedEvent socketAccepted;
    int port;
    ServerSocket listener;


    public SocketListener(int port, SocketAcceptedEvent socketAccepted) {
        this.port = port;
        this.socketAccepted = socketAccepted;
    }

    public void run() {
        System.out.println("SocketListener running!");

        try {
            // Initialize a new listener
            listener = new ServerSocket(port);

            while (true) {
                // Look for a socket to accept
                Socket socket = listener.accept();
                // Get callback
                socketAccepted.onSocketAccepted(socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
