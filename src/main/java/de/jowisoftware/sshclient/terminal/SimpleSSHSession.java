package de.jowisoftware.sshclient.terminal;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.events.EventHub;
import de.jowisoftware.sshclient.events.ReflectionEventHub;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.BufferStorage;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.buffer.Renderer;
import de.jowisoftware.sshclient.terminal.buffer.TabStopManager;
import de.jowisoftware.sshclient.terminal.events.DisplayType;
import de.jowisoftware.sshclient.terminal.events.KeyboardEvent;
import de.jowisoftware.sshclient.terminal.events.VisualEvent;
import de.jowisoftware.sshclient.terminal.gfx.GfxCharSetup;
import de.jowisoftware.sshclient.util.StringUtils;

public class SimpleSSHSession implements SSHSession {
    private static final Logger LOGGER = Logger.getLogger(SimpleSSHSession.class);

    private final Buffer buffer;
    private final Renderer renderer;
    private final EventHub<KeyboardEvent> keyboadFeedback =
            ReflectionEventHub.forEventClass(KeyboardEvent.class);
    private final EventHub<VisualEvent> visualEvents =
            ReflectionEventHub.forEventClass(VisualEvent.class);
    private final GfxCharSetup charSetup;
    private final TabStopManager tabstopManager;

    private DisplayType displayType;
    private OutputStream responseStream;

    private Thread backgroundRenderer;

    private final String name;

    public SimpleSSHSession(final String name,
            final Buffer buffer,
            final Renderer renderer,
            final GfxCharSetup charSetup,
            final TabStopManager tabstopManager) {
        this.buffer = buffer;
        this.charSetup = charSetup;
        this.tabstopManager = tabstopManager;
        this.renderer = renderer;
        this.name = name;

        initBackgroundRenderer();
    }

    private void initBackgroundRenderer() {
        backgroundRenderer = new Thread("BackgroundRenderer-" + name) {
            @Override
            public void run() {
                while(true) {
                    synchronized(this) {
                        try {
                            this.wait();
                        } catch (final InterruptedException e) {
                            Logger.getLogger(getClass()).error("Error in background-Renderer: " + e);
                        }
                    }
                    renderer.renderSnapshot(buffer.createSnapshot());
                }
            }
        };
        backgroundRenderer.setDaemon(true);
        backgroundRenderer.start();
    }

    @Override
    public Buffer getBuffer() {
        return buffer;
    }

    @Override
    public EventHub<KeyboardEvent> getKeyboardFeedback() {
        return keyboadFeedback;
    }

    @Override
    public EventHub<VisualEvent> getVisualFeedback() {
        return visualEvents;
    }

    @Override
    public GfxCharSetup getCharSetup() {
        return charSetup;
    }

    @Override
    public DisplayType getDisplayType() {
        return displayType;
    }

    @Override
    public void setDisplayType(final DisplayType newDisplayType) {
        displayType = newDisplayType;
    }

    public void setOutputStream(final OutputStream stream) {
        this.responseStream = stream;
    }

    @Override
    public void sendToServer(final String string) {
        sendToServer(
                ((Charset.defaultCharset()
                        .encode(string).array())));
    }

    @Override
    public void sendToServer(final byte[] bytes) {
        if (responseStream == null) {
            return;
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Sending: " +
                    StringUtils.escapeForLogs(bytes, 0, bytes.length));
        }
        synchronized(responseStream) {
            try {
                responseStream.write(bytes);
                responseStream.flush();
            } catch(final IOException e) {
                LOGGER.warn("Failed to send keypress: " +
                        StringUtils.escapeForLogs(bytes, 0, bytes.length), e);
            }
        }
    }

    @Override
    public TabStopManager getTabStopManager() {
        return tabstopManager;
    }

    @Override
    public Renderer getRenderer() {
        return renderer;
    }

    public void render() {
        synchronized(backgroundRenderer) {
            backgroundRenderer.notify();
        }
    }

    public Position translateMousePositionToCharacterPosition(final int x, final int y) {
        Position position = renderer.translateMousePosition(x, y);
        position = moveMousePositionInBufferRange(position);
        position = movePositionToMultibyteStart(position);
        return position;
    }

    private Position movePositionToMultibyteStart(Position position) {
        if (position.x > buffer.getSize().x) {
            return position;
        }

        while (buffer.getCharacter(position.y, position.x) == BufferStorage.EMPTY) {
            position = position.offset(-1, 0);
        }
        return position;
    }

    private Position moveMousePositionInBufferRange(Position position) {
        final Position bufferSize = buffer.getSize();
        position = position.moveInRange(
                new Position(bufferSize.x + 1, bufferSize.y).toRange());
        return position;
    }

    @Override
    public void saveState() {
        buffer.saveCursorPosition();
        charSetup.save();
    }

    @Override
    public void restoreState() {
        buffer.restoreCursorPosition();
        charSetup.restore();
    }
}
