package de.jowisoftware.sshclient.async;

import org.apache.log4j.Logger;

import java.util.Deque;
import java.util.LinkedList;

public class AsyncQueueRunner<T extends Runnable> extends Thread {
    private static final Logger LOGGER = Logger.getLogger(AsyncQueueRunner.class);
    protected final Object sync = new Object();

    private volatile boolean isRunning = true;
    protected final Deque<T> items = new LinkedList<>();

    private StatusListener listener = null;

    public AsyncQueueRunner(final String name) {
        super(name);
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

            final String name = item.toString();
            LOGGER.debug("Processing request \"" + name + "\", remaining items in queue: " + items.size());
            if (listener != null) {
                listener.beginAction(name);
            }

            item.run();

            if (listener != null) {
                listener.endAction(name);
            }
        } while(item != null);

        LOGGER.info("Lazy updater thread ended");
    }

    public void queue(final T item) {
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

        try {
            this.join();
        } catch (final InterruptedException e) {
            throw new RuntimeException("Could not wait for stopped thread",e);
        }

        LOGGER.info("Thread " + getName() + " ended");
    }

    public void setListener(final StatusListener listener) {
        this.listener = listener;
    }
}
