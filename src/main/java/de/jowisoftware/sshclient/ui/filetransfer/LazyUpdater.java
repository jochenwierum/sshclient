package de.jowisoftware.sshclient.ui.filetransfer;

import de.jowisoftware.sshclient.filetransfer.AbstractTreeNodeItem;
import de.jowisoftware.sshclient.filetransfer.ChildrenProvider;
import org.apache.log4j.Logger;

import java.util.Deque;
import java.util.LinkedList;

public class LazyUpdater<S extends AbstractTreeNodeItem<?>, T extends ChildrenProvider<S>> extends Thread {
    private static final Logger LOGGER = Logger.getLogger(LazyUpdater.class);
    private final Object sync = new Object();
    private volatile boolean isRunning = true;
    private final Deque<Runnable> items = new LinkedList<>();

    public LazyUpdater() {
        start();
    }

    @Override
    public void run() {
        Runnable item;
        do {
            synchronized (sync) {
                while (items.isEmpty() && isRunning) {
                    try {
                        LOGGER.debug("Waiting for work");
                        sync.wait();
                    } catch (final InterruptedException e) {
                        // we handle this as "normal" notify
                    }
                }

                if (!isRunning) {
                    break;
                }

                item = items.removeFirst();
            }

            LOGGER.debug("Processing next request, remaining items in queue: " + items.size());
            item.run();
        } while(item != null);

        LOGGER.info("Lazy updater thread ended");
    }

    public void queueUpdate(final Runnable item) {
        synchronized (sync) {
            LOGGER.debug("Queuing update");
            items.addFirst(item);
            sync.notify();
        }
    }

    public void shutdown() {
        synchronized (sync) {
            isRunning = false;
            sync.notify();
        }
    }
}
