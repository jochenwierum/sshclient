package de.jowisoftware.sshclient.filetransfer.operations;

public interface ExtendedProgressMonitor {
    void init(final long id, final int op, final String src, final String dest, final long max);

    void count(final long id, final long count);

    void end(final long id);
}
