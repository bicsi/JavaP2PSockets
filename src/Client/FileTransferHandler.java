package Client;

import Common.Constants;
import Common.GenericConnection;
import Common.SocketListener;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by lucian on 16.05.2017.
 */
public class FileTransferHandler {

    private SocketListener listener;

    private FileTransferHandler() {
        listener = new SocketListener(Constants.FILE_TRANSFER_PORT, s -> {
            System.out.println("Got a request to transfer file!");
            try {
                GenericConnection<String, byte[]> connection = new GenericConnection<>(s);
                try {
                    String filename = connection.getMessage();
                    Path path = Settings.getPublishedPath().resolve(Paths.get(filename));
                    System.out.println("Sending file " + path.toString());
                    connection.sendMessage(Files.readAllBytes(path));
                } catch (Exception e) {
                    e.printStackTrace();
                    connection.sendMessage(new byte[0]);
                }
            } catch (IOException e) {
                System.out.println("Partner offline!");
            }
        });
    }

    public void startListening() {
        System.out.println("FTH Started!");
        listener.start();
    }

    private static FileTransferHandler instance;
    public static FileTransferHandler getInstance() {
        if (instance == null)
            instance = new FileTransferHandler();
        return instance;
    }

    public void fetchFile(String ip, String filename) {
        if (ip == null || filename == null) {
            System.out.println("File not found");
            return;
        }
        System.out.println("IP: " + ip);
        System.out.println("filename: " + filename);

        try {
            Socket socket = new Socket(ip, Constants.FILE_TRANSFER_PORT);
            GenericConnection<byte[], String> connection = new GenericConnection<>(socket);
            connection.sendMessage(filename);
            byte[] result = connection.getMessage();
            if (result.length == 0) {
                System.out.println("Transfer failed!");
                return;
            }

            Path path = Settings.getPublishedPath().resolve(Paths.get(filename));
            Files.write(path, result);
            System.out.println("File " + path + " created (" + result.length + " bytes)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
