package de.jowisoftware.sshclient.filetransfer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileInfo implements Comparable<FileInfo> {
    private static final char suffixes[] = {' ', 'K', 'M', 'G', 'T', 'E', 'P'};

    private final String fullName;
    private final String name;
    private final String size;
    private final String owner;
    private final String group;
    private final String permissions;
    private final String modified;
    private final boolean directory;

    public FileInfo(
            final String fullName, final boolean isDirectory,
            final String name, final long size, final String owner,
            final String group, final String permissions, final Date modified) {
        this.fullName = fullName;
        this.name = name;
        this.size = niceSize(size);
        this.owner = owner;
        this.group = group;
        this.permissions = permissions;
        this.directory = isDirectory;

        final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        this.modified = df.format(modified);
    }

    private String niceSize(final long size) {
        double newSize = size;
        int count = 0;
        while (newSize >= 1024) {
            newSize /= 1024;
            ++count;
        }

        if (count == 0) {
            return size + " B";
        } else {
            return String.format("%3.02f %siB", newSize, suffixes[count]);
        }
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getOwner() {
        return owner;
    }

    public String getPermissions() {
        return permissions;
    }

    public String getGroup() {
        return group;
    }

    public String getModified() {
        return modified;
    }

    @Override
    public int compareTo(final FileInfo o) {
        return name.compareTo(o.name);
    }

    public boolean isDirectory() {
        return directory;
    }

    public String getFullName() {
        return fullName;
    }
}
