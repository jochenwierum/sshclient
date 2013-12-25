package de.jowisoftware.sshclient.filetransfer.operations;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.apache.log4j.Logger;

import java.util.Deque;
import java.util.LinkedList;

public class OperationExecutor extends Thread {
    private static final Logger LOGGER = Logger.getLogger(OperationExecutor.class);

    private final Object sync = new Object();
    private final Deque<OperationCommand> operationQueue = new LinkedList<>();
    private volatile boolean running = true;

    private final AbortableSftpProgressMonitorWrapper monitor;
    private final ChannelSftp channel;

    public OperationExecutor(final ChannelSftp channel, final ExtendedProgressMonitor monitor) {
        this.channel = channel;
        this.monitor = new AbortableSftpProgressMonitorWrapper(monitor);
        start();
    }

    @Override
    public void run() {
        while(running) {
            OperationCommand item = null;
            synchronized (sync) {
                while(running && (item = operationQueue.poll()) == null) {
                    try {
                        sync.wait();
                    } catch (final InterruptedException e) {
                        // handle as normal "notify"
                    }
                }

                if (!running) {
                    break;
                }
            }

            process(item);
        }
    }

    private void process(final OperationCommand item) {
        try {
            item.execute(channel, monitor.getFor(item));
        } catch (final SftpException e) {
            LOGGER.error("Could not execute operation " + item, e);
        }
    }

    public void shutdown() {
        monitor.abort();
        running = false;
        synchronized (sync) {
            sync.notify();
        }

        try {
            this.join();
        } catch (final InterruptedException e) {
            LOGGER.error("Could not wait for stopped thread", e);
        }
        LOGGER.info("Executor thread ended");
    }

    public void queue(final OperationCommand command) {
        synchronized (sync) {
            operationQueue.addLast(command);
            sync.notify();
        }
    }

    public void dequeueAndAbort(final OperationCommand command) {
        synchronized (sync) {
            operationQueue.remove(command);
            command.abort();
        }
    }
}
