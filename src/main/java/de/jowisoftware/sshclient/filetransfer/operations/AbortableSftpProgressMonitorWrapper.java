package de.jowisoftware.sshclient.filetransfer.operations;

import com.jcraft.jsch.SftpProgressMonitor;

public final class AbortableSftpProgressMonitorWrapper {
    private final ExtendedProgressMonitor monitor;
    private volatile boolean aborted = false;

    public AbortableSftpProgressMonitorWrapper(final ExtendedProgressMonitor monitor) {
        this.monitor = monitor;
    }

    public void abort() {
        aborted = true;
    }

    public SftpProgressMonitor getFor(final OperationCommand command) {
        return new SftpProgressMonitor() {
            @Override
            public void init(final int op, final String src, final String dest, final long max) {
                monitor.init(command.id(), op, src, dest, max);
            }

            @Override
            public boolean count(final long count) {
                monitor.count(command.id(), count);
                return !aborted && !command.isAborted();
            }

            @Override
            public void end() {
                monitor.end(command.id());
            }
        };
    }
}
