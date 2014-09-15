package de.jowisoftware.sshclient.filetransfer;

public interface ChildrenProvider<T extends AbstractTreeNodeItem<?>> {
    T[] getChildrenOf(T node);
    T[] getRoots();
    FileInfo[] getFiles(T node);
    long getSize(T node);
}
