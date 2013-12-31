package de.jowisoftware.sshclient.filetransfer.operations;

public abstract class AbstractOperationCommand implements OperationCommand {
    private static long nextId = 1;
    private final long id;
    private boolean aborted = false;

    protected AbstractOperationCommand() {
        id = nextId++;
    }

    @Override
    public final long id() {
        return id;
    }

    @Override
    public final void abort() {
        aborted = true;
    }

    @Override
    public boolean isAborted() {
        return aborted;
    }
}
