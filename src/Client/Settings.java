package Client;

import java.nio.file.Path;

/**
 * Created by lucian on 15.05.2017.
 */

public class Settings {
    private static String username;
    private static Path publishedPath;

    public static Path getPublishedPath() {
        return publishedPath;
    }
    public static void setPublishedPath(Path path) {
        publishedPath = path;
    }

    public static void setUsername(String name) {
        username = name;
    }
    public static String getUsername() {
        return username;
    }
}
