package de.jowisoftware.sshclient.filetransfer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileInfo {
    private final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final char suffixes[] = {' ', 'K', 'M', 'G', 'T', 'E', 'P'};

    private String name;
    private String size;
    private String owner;
    private String group;
    private String permissions;
    private String modified;
    private String created;

    public FileInfo(final String name, final long size, final String owner, final String group, final String permissions,
            final Date created, final Date modified) {
        this.name = name;
        this.size = niceSize(size);
        this.owner = owner;
        this.group = group;
        this.permissions = permissions;
        this.created = df.format(created);
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

    public String getCreated() {
        return created;
    }

    public String getModified() {
        return modified;
    }
}
