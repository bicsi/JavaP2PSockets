package Client;

import Client.Utils.HeartBeater;
import Common.Constants;
import Common.MessageUtils.ClientMessage;
import Common.GenericConnection;
import Common.MessageUtils.ClientMessageType;
import Common.MessageUtils.ServerMessage;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by lucian on 15.05.2017.
 */
public class ServerConnection extends GenericConnection<ServerMessage, ClientMessage> {
    String ip;
    private HeartBeater heartBeater;

    public ServerConnection(Socket s) throws IOException {
        super(s);
        heartBeater = new HeartBeater(this);
        heartBeater.start();
    }

    @Override
    public String toString() {
        return ip;
    }

    @Override
    public void close() throws IOException {
        sendMessage(ClientMessageBuilder.buildDisconnect());
        super.close();
        heartBeater.interrupt();
    }
}
