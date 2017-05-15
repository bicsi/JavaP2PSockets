package Common.MessageUtils;

import java.io.Serializable;

/**
 * Created by lucian on 15.05.2017.
 */
public class ServerMessage implements Serializable {
    public final ServerMessageType type;
    public final Serializable arg;

    public ServerMessage(ServerMessageType type, Serializable arg) {
        this.type = type;
        this.arg = arg;
    }
}
