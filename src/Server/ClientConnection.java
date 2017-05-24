package Server;

import Common.MessageUtils.ClientMessage;
import Common.GenericConnection;
import Common.MessageUtils.ServerMessage;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;

/**
 * Created by lucian on 15.05.2017.
 */
public class ClientConnection
        extends GenericConnection<ClientMessage, ServerMessage> {
    private String ip, canonicalIp, username;
    private int port;
    private LocalDateTime lastHeartbeat;

    public ClientConnection(Socket s) throws IOException {
        super(s);
        canonicalIp = s.getInetAddress().getCanonicalHostName();
        ip = s.getInetAddress().getHostName();
        username = "anonymouse";
        beat();
    }

    @Override
    public String toString() {
        return username + "@" + canonicalIp;
    }

    public void initialize(String name, int port) {
        username = name;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }
    public int getPort() {
        return port;
    }

    public LocalDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void beat() {
        lastHeartbeat = LocalDateTime.now();
    }
}
