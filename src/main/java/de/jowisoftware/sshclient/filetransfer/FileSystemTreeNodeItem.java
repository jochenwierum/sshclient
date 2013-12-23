package de.jowisoftware.sshclient.filetransfer;

import java.io.File;

public class FileSystemTreeNodeItem extends AbstractTreeNodeItem {
    private final File file;

    public FileSystemTreeNodeItem(final File file) {
        this.file = file;
    }

    public String toString() {
        if (file.getName().isEmpty()) {
            return file.toString();
        } else {
            return file.getName();
        }
    }

    public File getFile() {
        return file;
    }
}
