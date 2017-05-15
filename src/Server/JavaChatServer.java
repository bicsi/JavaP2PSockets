package Server;

import Common.Constants;
import Common.SocketListener;

import java.io.IOException;

/**
 * Created by lucian on 15.05.2017.
 */
public class JavaChatServer {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting Server...");

        ClientManager manager = ClientManager.getInstance();
        SocketListener processor = new SocketListener(Constants.PORT, s -> {
            try {
                System.out.println("Client connected!");
                ClientConnection connection = new ClientConnection(s);
                manager.addClient(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        manager.start();
        processor.start();

        while (true) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {}
        }
    }
}
