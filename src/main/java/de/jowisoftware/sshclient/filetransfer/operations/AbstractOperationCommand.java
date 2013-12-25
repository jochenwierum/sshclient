package de.jowisoftware.sshclient.filetransfer.operations;

public abstract class AbstractOperationCommand implements OperationCommand {
    protected final long id;
    private boolean aborted = false;

    protected AbstractOperationCommand(final long id) {
        this.id = id;
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
