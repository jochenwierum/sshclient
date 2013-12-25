package de.jowisoftware.sshclient.filetransfer;

public class SftpTreeNodeItem extends AbstractTreeNodeItem<SftpTreeNodeItem> {
    private final String path;
    private final String filename;

    SftpTreeNodeItem(final String filename, final String path) {
        this.path = path;
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public String toString() {
        return filename;
    }
}
