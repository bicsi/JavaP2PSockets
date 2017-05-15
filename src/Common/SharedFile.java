package Common;

import Server.ClientConnection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by lucian on 15.05.2017.
 */

public class SharedFile implements Serializable {
    public final String owner;
    public final String path;
    public final int fileId;

    public SharedFile(String owner, String path, int fileId) {
        this.path = path;
        this.owner = owner;
        this.fileId = fileId;
    }

    @Override
    public String toString() {
        return "[" + fileId + "] {" + owner + "} " + path.toString();
    }
}
