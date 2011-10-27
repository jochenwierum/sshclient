package de.jowisoftware.sshclient.terminal;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.events.EventHub;
import de.jowisoftware.sshclient.events.LinkedListEventHub;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.util.StringUtils;

public class DefaultSession<T extends GfxChar> implements Session<T> {
    private static final Logger LOGGER = Logger.getLogger(DefaultSession.class);

    private final Buffer<T> buffer;
    private final EventHub<KeyboardEvent> keyboadFeedback =
            LinkedListEventHub.forEventClass(KeyboardEvent.class);
    private final EventHub<VisualEvent> visualEvents =
            LinkedListEventHub.forEventClass(VisualEvent.class);
    private final GfxCharSetup<T> charSetup;

    private DisplayType displayType;
    private OutputStream responseStream;

    public DefaultSession(final Buffer<T> buffer,
            final GfxCharSetup<T> charSetup) {
        this.buffer = buffer;
        this.charSetup = charSetup;
    }

    @Override
    public Buffer<T> getBuffer() {
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
    public GfxCharSetup<T> getCharSetup() {
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
}
