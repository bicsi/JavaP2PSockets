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
        FileTransferHandler fth = FileTransferHandler.getInstance();

        System.out.println("Enter username: ");
        user.setUsername(scanner.nextLine());

        String ip = Constants.SERVER_IP;
        System.out.println("Enter server ip [leave blank for default]: ");
        String line = scanner.nextLine();
        if (!line.isEmpty()) ip = line;

        fth.startListening();

        // Connect to socket
        System.out.println("Connecting...");
        ServerConnection connection;
        try {
            Socket socket = new Socket(ip, Constants.PORT);
            connection = new ServerConnection(socket);
            System.out.println("Connected!");
            connection.sendMessage(ClientMessageBuilder.buildSetUsername(user.getUsername()));
        } catch (Exception e) {
            System.out.println("Could not connect to server");
            return;
        }
        while (connection.isConnected()) {
            try {
                System.out.print("> ");
                String[] command = scanner.nextLine().split(" ");
                switch (command[0]) {
                    case "publish":
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
                        fth.fetchFile(map.get("ip"), map.get("filename"));

                        break;
                    case "disconnect":
                        connection.sendMessage(ClientMessageBuilder.buildDisconnect());
                        connection.disconnect();

                        break;
                    default:
                        System.out.println("Unrecognized command, try again.");

                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
