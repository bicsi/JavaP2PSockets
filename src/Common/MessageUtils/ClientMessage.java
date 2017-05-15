package Common.MessageUtils;

import java.io.Serializable;

public class ClientMessage implements Serializable {
    public final ClientMessageType type;
    public final Serializable arg;

    public ClientMessage(ClientMessageType type, Serializable arg) {
        this.type = type; this.arg = arg;
    }
}
