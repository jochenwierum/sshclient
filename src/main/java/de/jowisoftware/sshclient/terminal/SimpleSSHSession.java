package de.jowisoftware.sshclient.terminal;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.events.EventHub;
import de.jowisoftware.sshclient.events.ReflectionEventHub;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.BufferStorage;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.buffer.Renderer;
import de.jowisoftware.sshclient.terminal.buffer.TabStopManager;
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

    private OutputStream responseStream;
    private final Charset charset;

    private BackgroundRenderThread backgroundRenderer;

    private final String name;

    public SimpleSSHSession(final String name,
            final Buffer buffer,
            final Renderer renderer,
            final GfxCharSetup charSetup,
            final TabStopManager tabstopManager,
            final Charset charset) {
        this.buffer = buffer;
        this.charSetup = charSetup;
        this.tabstopManager = tabstopManager;
        this.renderer = renderer;
        this.charset = charset;
        this.name = name;

        initBackgroundRenderer();
    }

    private void initBackgroundRenderer() {
        backgroundRenderer = new BackgroundRenderThread(name, renderer, buffer);
        backgroundRenderer.setDaemon(true);
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

    public void setOutputStream(final OutputStream stream) {
        this.responseStream = stream;
    }

    @Override
    public void sendToServer(final String string) {
        sendToServer(string.toCharArray());
    }

    @Override
    public void sendToServer(final char[] chars) {
        final byte[] bytes = charset.encode(CharBuffer.wrap(chars)).array();
        rawSendToServer(bytes);
    }

    @Override
    public void rawSendToServer(final byte[] bytes) {
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
                LOGGER.warn("Failed to send string: " +
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

    @Override
    public void render() {
        backgroundRenderer.render();
    }

    public void pauseRendering() {
        backgroundRenderer.pauseRendering();
    }

    public void resumeRendering() {
        backgroundRenderer.resumeRendering();
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

    @Override
    public void setRenderOffset(final int newOffset) {
        backgroundRenderer.setRenderOffset(newOffset);
    }

    public void startRenderer() {
        if (backgroundRenderer.isAlive()) {
            throw new IllegalStateException("BackgroundRenderer is already running");
        }
        backgroundRenderer.start();
    }
}
