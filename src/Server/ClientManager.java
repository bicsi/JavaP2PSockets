package Server;

import Common.Constants;
import Common.MessageUtils.ClientMessage;
import Common.SharedFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by lucian on 15.05.2017.
 */
public class ClientManager extends Thread {

    private Queue<ClientConnection> connections;
    private int sleepTimeMs;
    private FileManager fileManager;

    private ClientManager() {
        this.sleepTimeMs = Constants.CLIENT_MANAGER_SLEEP_TIME;
        this.fileManager = FileManager.getInstance();
        connections = new ConcurrentLinkedQueue<>();
    }

    private static ClientManager instance;
    public static ClientManager getInstance() {
        if (instance == null)
            instance = new ClientManager();
        return instance;
    }

    public void run() {
        System.out.println("ClientManager running!");

        while (true) {
            if (!connections.isEmpty()) {
                // Get client in front of the queue
                ClientConnection currentClient = connections.poll();
                if (!currentClient.isConnected()) continue;

                try {
                    if (currentClient.hasMessage()) {
                        ClientMessage message = currentClient.getMessage();
                        processMessage(currentClient, message);
                    }
                    // Throw client back to the queue
                    connections.add(currentClient);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Disconnecting client...");
                    currentClient.disconnect();
                }
            }

            // Sleep
            try {
                sleep(sleepTimeMs);
            } catch (InterruptedException e) {}
        }
    }

    public synchronized List<ClientConnection> getClientList() {
        List<ClientConnection> ret = new ArrayList<>();
        ret.addAll(connections);
        return ret;
    }

    private void processMessage(ClientConnection currentClient, ClientMessage message) {
        Serializable arg = message.arg;
        try {
            switch (message.type) {
                case MSG_SET_USERNAME:
                    System.out.println(currentClient + " set its name to " + arg);
                    currentClient.setUsername((String) arg);
                    break;
                case MSG_PUBLISH:
                    List<String> filenames = (List<String>) arg;
                    System.out.println(currentClient + " added " + filenames.size() + " files");
                    fileManager.updateFiles(currentClient, filenames);
                    break;
                case MSG_SEARCH_FILE:
                    System.out.println(currentClient + " searched for: " + arg);
                    List<SharedFile> result = fileManager.queryFiles((String) arg);
                    currentClient.sendMessage(ServerMessageBuilder.buildSearchResult(result));
                    break;
                case MSG_FETCH_FILE:
                    System.out.println(currentClient + " requested file " + arg);
                    ClientConnection owner = fileManager.getOwner((int) arg);
                    SharedFile file = fileManager.getFileById((int) arg);

                    currentClient.sendMessage(ServerMessageBuilder.buildOwnerIp(
                            owner == null ? null : owner.getIp(),
                            file == null ? null : file.path));
                    break;
                case MSG_DISCONNECT:
                    currentClient.disconnect();
            }
        } catch (Exception e) {
            System.out.println("Message processor failed.");
            e.printStackTrace();
        }
    }

    public void addClient(ClientConnection connection) {
        connections.add(connection);
    }
}
