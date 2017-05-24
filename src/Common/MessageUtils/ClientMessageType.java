package Common.MessageUtils;

/**
 * Created by lucian on 15.05.2017.
 */

public enum ClientMessageType {
    MSG_INIT(1),
    MSG_PUBLISH(2),
    MSG_SEARCH_FILE(3),
    MSG_DISCONNECT(4),
    MSG_FETCH_FILE(5),
    MSG_HEARTBEAT(6);

    private final int type;
    ClientMessageType(int type) {
        this.type = type;
    }
}
