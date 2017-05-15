package Client.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucian on 15.05.2017.
 */
public class FileManager {
    private DirectoryStream.Filter<Path> filter = entry -> {
        File f = entry.toFile();
        return !f.isHidden();
    };

    private void recGetFilenames(List<String> filenames, Path root, Path dir, boolean recurse) throws IOException {
        DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter);
        for (Path path : stream) {
            if(path.toFile().isDirectory() && recurse) {
                recGetFilenames(filenames, root, path, recurse);
            } else {
                filenames.add(root.relativize(path).toString());
            }
        }
    }

    public List<String> getFilenames(Path root, boolean recurse) throws IOException {
        List<String> ret = new ArrayList<>();
        recGetFilenames(ret, root, root, recurse);
        return ret;
    }
}
