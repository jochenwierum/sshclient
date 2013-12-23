package de.jowisoftware.sshclient.filetransfer;

public class AbstractTreeNodeItem {
    private boolean isLoaded = false;

    public void markAsLoaded() {
        isLoaded = true;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

}
