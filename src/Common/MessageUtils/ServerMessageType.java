package Common.MessageUtils;

/**
 * Created by lucian on 15.05.2017.
 */
public enum ServerMessageType {
    MSG_SEARCH_RESULT(1),
    MSG_OWNER_IP(2);

    private final int type;
    ServerMessageType(int type) {
        this.type = type;
    }
}
