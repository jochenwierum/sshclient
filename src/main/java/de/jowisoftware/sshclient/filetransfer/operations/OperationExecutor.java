package de.jowisoftware.sshclient.filetransfer.operations;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import de.jowisoftware.sshclient.async.AsyncQueueRunner;
import org.apache.log4j.Logger;

import java.util.Iterator;

public class OperationExecutor extends AsyncQueueRunner<OperationExecutor.WrapperThread> {
    private static final Logger LOGGER = Logger.getLogger(OperationExecutor.class);

    private AbortableSftpProgressMonitorWrapper monitor;
    private final ChannelSftp channel;

    class WrapperThread extends Thread {
        private final OperationCommand item;

        WrapperThread(final OperationCommand item) {
            this.item = item;
        }

        @Override
        public void run() {
            final SftpProgressMonitor sftpProgressMonitor = monitor.getFor(item);
            try {
                item.execute(channel, sftpProgressMonitor);
            } catch (final SftpException e) {
                LOGGER.error("Could not execute operation " + item, e);
                sftpProgressMonitor.end();
            }
        }

        @Override
        public String toString() {
            return item.toString();
        }

        public OperationCommand getItem() {
            return item;
        }
    }

    public OperationExecutor(final String name, final ChannelSftp channel) {
        super(name);
        this.channel = channel;
    }

    public void setMonitor(final ExtendedProgressMonitor monitor) {
        this.monitor = new AbortableSftpProgressMonitorWrapper(monitor);;
    }

    @Override
    public void shutdown() {
        monitor.abort();
        super.shutdown();
    }

    public void queue(final OperationCommand item) {
        super.queue(new WrapperThread(item));
    }

    public void dequeueAndAbort(final OperationCommand command) {
        synchronized (sync) {
            @SuppressWarnings("unchecked")
            final Iterator<WrapperThread> iterator = items.iterator();
            while (iterator.hasNext()) {
                final WrapperThread thread = iterator.next();
                if (thread.getItem() == command) {
                    command.abort();
                    iterator.remove();
                    break;
                }
            }
        }
    }
}
