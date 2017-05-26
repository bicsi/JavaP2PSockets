package Server;

import Common.SharedFile;

import java.nio.file.Paths;
import java.util.*;

public class FileManager {

    private Map<ClientConnection, List<SharedFile>> filesMap = new HashMap<>();
    private Map<Integer, ClientConnection> ownerMap = new HashMap<>();
    private Map<Integer, SharedFile> idMap = new HashMap<>();
    private Set<SharedFile> allFiles = new HashSet<>();
    private Set<ClientConnection> owners = new HashSet<>();

    private int filesCount;

    private FileManager() {}

    private static FileManager instance;
    public static FileManager getInstance() {
        if (instance == null)
            instance = new FileManager();
        return instance;
    }

    public void clearFiles(ClientConnection owner) {
        owners.remove(owner);
        if (filesMap.containsKey(owner)) {
            for (SharedFile file : filesMap.get(owner)) {
                allFiles.remove(file);
                ownerMap.remove(file.fileId);
                idMap.remove(file.fileId);
            }
        }
        filesMap.remove(owner);
    }

    public void updateFiles(ClientConnection owner, List<String> files) {
        clearFiles(owner);

        List<SharedFile> newFiles = new ArrayList<>();
        for (String path : files) {
            SharedFile file = new SharedFile(owner.toString(), path, ++filesCount);
            allFiles.add(file);
            allFiles.add(file);
            newFiles.add(file);
            ownerMap.put(file.fileId, owner);
            idMap.put(file.fileId, file);
        }

        filesMap.put(owner, newFiles);
        owners.add(owner);
    }

    public List<SharedFile> queryFiles(String queryString) {
        List<SharedFile> ret = new ArrayList<>();
        for (SharedFile file : allFiles) {
            String filename = Paths.get(file.path).getFileName().toString();
            ClientConnection owner = ownerMap.get(file.fileId);
            if (!owner.isConnected()) {
                clearFiles(owner);
            }

            if (filename.contains(queryString))
                ret.add(file);
        }
        return ret;
    }

    ClientConnection getOwner(int id) {
        return ownerMap.getOrDefault(id, null);
    }

    SharedFile getFileById(int id) {
        return idMap.getOrDefault(id, null);
    }
}
