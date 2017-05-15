package Server;

import Common.MessageUtils.ClientMessage;
import Common.GenericConnection;
import Common.MessageUtils.ServerMessage;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by lucian on 15.05.2017.
 */
public class ClientConnection
        extends GenericConnection<ClientMessage, ServerMessage> {
    private String ip, canonicalIp, username;

    public ClientConnection(Socket s) throws IOException {
        super(s);
        canonicalIp = s.getInetAddress().getCanonicalHostName();
        ip = s.getInetAddress().getHostName();
        username = "anonymouse";
    }

    @Override
    public String toString() {
        return username + "@" + canonicalIp;
    }

    public void setUsername(String name) {
        username = name;
    }

    public String getIp() {
        return ip;
    }
}
