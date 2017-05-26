package Server;

import Common.Constants;
import Common.MessageUtils.ClientMessage;
import Common.SharedFile;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by lucian on 15.05.2017.
 */
public class ClientManager extends Thread {

    private Queue<ClientConnection> connections;
    private int sleepTimeMs;
    private FileManager fileManager;
//    private Map<String, ClientConnection> ipMap;

    private ClientManager() {
        this.sleepTimeMs = Constants.CLIENT_MANAGER_SLEEP_TIME;
        this.fileManager = FileManager.getInstance();
        connections = new ConcurrentLinkedQueue<>();
//        ipMap = new HashMap<>();
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
                    while (currentClient.hasMessage()) {
                        ClientMessage message = currentClient.getMessage();
                        processMessage(currentClient, message);
                    }

                    if (LocalDateTime.now().minusNanos(1000000L * Constants.REACHABLE_TIMEOUT).compareTo(
                            currentClient.getLastHeartbeat()) < 0) {
                        // Throw client back to the queue
                        connections.add(currentClient);
                    } else {
                        System.out.println("Client " + currentClient + " timed out.");
                    }
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
                case MSG_HEARTBEAT:
                    currentClient.beat();
                    break;
                case MSG_INIT:
                    String[] args = ((String) arg).split(":");
                    String name = args[0];
                    int port = Integer.parseInt(args[1]);

                    System.out.println(currentClient + " initialized as " + arg);
                    currentClient.initialize(name, port);
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
                            owner == null ? -1 : owner.getPort(),
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
//        String ip = connection.getIp();
//        int port = connection.getPort();
//        ClientConnection old = ipMap.getOrDefault(ip + ":" + port, null);
//        if (old != null) {
//            System.out.println("Disconnecting old instance of " + ip);
//            old.disconnect();
//        }
        connections.add(connection);
//        ipMap.put(ip + ":" + port, connection);
    }
}
