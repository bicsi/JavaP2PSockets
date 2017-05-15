package Common;

import java.net.Socket;

/**
 * Created by lucian on 15.05.2017.
 */

public interface SocketAcceptedEvent {
    void onSocketAccepted(Socket s);
}
