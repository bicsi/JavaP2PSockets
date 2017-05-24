package Client.Utils;

import Client.ServerConnection;
import Common.Constants;
import Common.MessageUtils.ClientMessage;
import Common.MessageUtils.ClientMessageType;

import java.io.IOException;

/**
 * Created by lucian on 24.05.2017.
 */
public class HeartBeater extends Thread {
    public ServerConnection connection;

    public HeartBeater(ServerConnection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        while (connection.isConnected()) {
            try {
                connection.sendMessage(new ClientMessage(ClientMessageType.MSG_HEARTBEAT, null));
            } catch (IOException e) {
                connection.disconnect();
            }
            try {sleep(Constants.CLIENT_MANAGER_SLEEP_TIME / 2);}
            catch (InterruptedException e) {}
        }
    }
}
