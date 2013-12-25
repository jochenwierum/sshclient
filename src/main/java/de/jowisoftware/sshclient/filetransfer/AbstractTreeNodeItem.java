package de.jowisoftware.sshclient.filetransfer;

public abstract class AbstractTreeNodeItem<T> implements Comparable<T> {
    private boolean isLoaded = false;

    public void markAsLoaded() {
        isLoaded = true;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public final int compareTo(final T o) {
        return toString().compareTo(o.toString());
    }
}
