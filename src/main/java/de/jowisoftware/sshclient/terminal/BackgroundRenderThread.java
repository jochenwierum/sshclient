package de.jowisoftware.sshclient.terminal;

import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class BackgroundRenderThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackgroundRenderThread.class);

    private volatile boolean run = true;
    private volatile boolean paused = false;
    private volatile boolean disposing = false;

    private final Renderer renderer;
    private final Buffer buffer;
    private int renderOffset;

    BackgroundRenderThread(final String name, final Renderer renderer, final Buffer buffer) {
        super("BackgroundRenderer-" + name);
        this.renderer = renderer;
        this.buffer = buffer;
    }

    public void setRenderOffset(final int renderOffset) {
        this.renderOffset = renderOffset;
        render();
    }

    @Override
    public void run() {
        while(!disposing) {
            synchronized(this) {
                while(!run && !paused) {
                    try {
                        this.wait();
                    } catch (final InterruptedException e) {
                        LOGGER.error("Error in background renderer", e);
                    }
                }
                run = false;
            }

            try {
                renderer.renderSnapshot(
                        buffer.createSnapshot().createSimpleSnapshot(renderOffset));
            } catch(final RuntimeException e) {
                LOGGER.error("background rendering failed", e);
            }
        }
        LOGGER.info("background rendering thread ended: {}", getName());
    }

    public void render() {
        synchronized(this) {
            run = true;
            this.notify();
        }
    }

    public void pauseRendering() {
        paused = true;
        while(run && this.isAlive()) {
            try {
                Thread.sleep(10);
            } catch (final InterruptedException e) {
                // ignore interruption - continue loop
            }
        }
    }

    public void resumeRendering() {
        paused = false;
        render();
    }

    public void dispose() {
        disposing = true;
        resumeRendering();
        try {
            join();
        } catch (final InterruptedException e) {
            LOGGER.error("Interrupted while waiting for background renderer to finish", e);
        }
    }
}
