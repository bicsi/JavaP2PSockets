package Common;

import java.io.*;
import java.net.Socket;

/**
 * Created by lucian on 15.05.2017.
 */
public class GenericConnection<TMessageIn, TMessageOut> implements Closeable {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private boolean connected;

    public GenericConnection(Socket s) throws IOException {
        this.socket = s;
        outputStream = new ObjectOutputStream(s.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(s.getInputStream());
        connected = true;
    }

    public boolean hasMessage() throws IOException {
        int ret = socket.getInputStream().available();
        return ret > 0;
    }

    public TMessageIn getMessage() throws IOException, ClassNotFoundException {
        return (TMessageIn) inputStream.readObject();
    }
    public void sendMessage(TMessageOut message) throws IOException {
        outputStream.writeObject(message);
        outputStream.flush();
    }

    public void disconnect() {
        if (!connected) return;
        System.out.println("Disconnecting " + this);

        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inputStream = null;
            outputStream = null;
            socket = null;
            connected = false;
        }
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
