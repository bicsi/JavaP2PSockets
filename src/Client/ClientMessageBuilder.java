package Client;

import Common.MessageUtils.ClientMessage;
import Common.MessageUtils.ClientMessageType;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lucian on 15.05.2017.
 */
public class ClientMessageBuilder {
    public static ClientMessage buildInit(String username, int port) {
        return new ClientMessage(ClientMessageType.MSG_INIT,
                username + ":" + ((Integer) port).toString());
    }

    public static ClientMessage buildDisconnect() {
        return new ClientMessage(ClientMessageType.MSG_DISCONNECT, null);
    }

    public static ClientMessage buildPublish(List<String> filenames) {
        return new ClientMessage(ClientMessageType.MSG_PUBLISH, (Serializable) filenames);
    }

    public static ClientMessage buildSearch(String query) {
        return new ClientMessage(ClientMessageType.MSG_SEARCH_FILE, query);
    }

    public static ClientMessage buildFetch(int id) {
        return new ClientMessage(ClientMessageType.MSG_FETCH_FILE, id);
    }
}
