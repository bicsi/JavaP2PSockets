package Client;

import Client.Utils.FileManager;
import Common.Constants;
import Common.MessageUtils.ServerMessage;
import Common.MessageUtils.ServerMessageType;
import Common.SharedFile;

import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class JavaChatClient {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Settings user = new Settings();

        System.out.println("Enter username: ");
        user.setUsername(scanner.nextLine());
        System.out.println("Enter file transfer port: ");
        Constants.FILE_TRANSFER_PORT = scanner.nextInt();

        FileTransferHandler fth = FileTransferHandler.getInstance();
        fth.startListening();

        ServerConnection connection = null;
        while (true) {
            try {
                System.out.print("> ");
                String[] command = scanner.nextLine().split(" ");
                switch (command[0]) {
                    case "connect":
                        System.out.println("Connecting...");
                        String ip = Constants.SERVER_IP;
                        if (command.length == 2)
                            ip = command[1];
                        try {
                            Socket socket = new Socket(ip, Constants.PORT);
                            connection = new ServerConnection(socket);
                            System.out.println("Connected!");
                            connection.sendMessage(ClientMessageBuilder.buildInit(user.getUsername(),
                                    Constants.FILE_TRANSFER_PORT));
                        } catch (Exception e) {
                            System.out.println("Could not connect to server");
                        }

                        break;
                    case "publish":
                        if (connection == null || !connection.isConnected()) {
                            System.out.println("Not connected!");
                            break;
                        }
                        try {
                            Path path = FileSystems.getDefault().getPath(command[1]);
                            List<String> filenames = new FileManager().getFilenames(path, Constants.RECURSE);
                            System.out.println("Publishing " + filenames.size() + " files...");
                            connection.sendMessage(ClientMessageBuilder.buildPublish(filenames));
                            Settings.setPublishedPath(path);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    case "search":
                        if (connection == null || !connection.isConnected()) {
                            System.out.println("Not connected!");
                            break;
                        }

                        String query = command[1];
                        connection.sendMessage(ClientMessageBuilder.buildSearch(query));
                        ServerMessage result = connection.getMessage();
                        if (result.type != ServerMessageType.MSG_SEARCH_RESULT)
                            throw new Exception("Server message type is of wrong format");
                        List<SharedFile> list = (List<SharedFile>) result.arg;

                        System.out.println(list.size() + " results for " + query + ":");
                        list.forEach(System.out::println);

                        break;
                    case "fetch":
                        if (connection == null || !connection.isConnected()) {
                            System.out.println("Not connected!");
                            break;
                        }

                        int id;
                        if (Settings.getPublishedPath() == null) {
                            System.out.println("You have to publish first!");
                            break;
                        }
                        try {
                            id = Integer.parseInt(command[1]);
                        } catch (NumberFormatException e) {
                            System.out.println("You have to specify an integer as the id of the file!");
                            break;
                        }
                        connection.sendMessage(ClientMessageBuilder.buildFetch(id));
                        result = connection.getMessage();
                        if (result.type != ServerMessageType.MSG_OWNER_IP)
                            throw new Exception("Server message type is of wrong format");

                        Map<String, String> map = (Map<String, String>) result.arg;
                        fth.fetchFile(map.get("ip"), Integer.parseInt(map.get("port")), map.get("filename"));

                        break;
                    case "disconnect":
                        if (connection == null || !connection.isConnected()) {
                            System.out.println("Not connected!");
                            break;
                        }

                        connection.sendMessage(ClientMessageBuilder.buildDisconnect());
                        connection.disconnect();
                        connection = null;

                        break;
                    default:
                        System.out.println("Unrecognized command, try again.");
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (connection != null)
                    connection.disconnect();
            }
        }
    }
}
