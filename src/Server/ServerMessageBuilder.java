package Server;

import Common.MessageUtils.ServerMessage;
import Common.MessageUtils.ServerMessageType;
import Common.SharedFile;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lucian on 15.05.2017.
 */
public class ServerMessageBuilder {
    public static ServerMessage buildSearchResult(List<SharedFile> result) {
        return new ServerMessage(ServerMessageType.MSG_SEARCH_RESULT, (Serializable) result);
    }

    public static ServerMessage buildOwnerIp(String ip, int port, String filename) {
        Map<String, String> map = new HashMap<>();
        map.put("ip", ip);
        map.put("filename", filename);
        map.put("port", ((Integer) port).toString());

        return new ServerMessage(ServerMessageType.MSG_OWNER_IP, (Serializable) map);
    }
}
