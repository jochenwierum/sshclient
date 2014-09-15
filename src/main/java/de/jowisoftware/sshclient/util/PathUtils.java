package de.jowisoftware.sshclient.util;

public final class PathUtils {
    private PathUtils() {}

    public static String concatUnixPathes(String base, String file) {
        if (base.endsWith("/")) {
            return base + file;
        } else if (base.isEmpty()) {
            return file;
        } else {
            return base + '/' + file;
        }
    }
}
