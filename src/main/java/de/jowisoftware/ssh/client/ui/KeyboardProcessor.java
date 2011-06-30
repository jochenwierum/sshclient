package de.jowisoftware.ssh.client.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;

import de.jowisoftware.ssh.client.util.StringUtils;

public class KeyboardProcessor implements KeyListener {
    private static final Logger LOGGER = Logger.getLogger(SSHConsole.class);
    private final OutputStream responseStream;

    private final boolean firstKeyPress = false;

    public KeyboardProcessor(final OutputStream outputStream) {
        this.responseStream = outputStream;
    }

    @Override
    public void keyTyped(final KeyEvent e) {
    }

    private void send(final byte... value) {
        for (final byte b : value) {
            send(b);
        }
    }

    private void send(final byte value) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Sending: " + StringUtils.byteToHex(value));
        }
        try {
            responseStream.write(value);
            responseStream.flush();
        } catch (final IOException e1) {
            LOGGER.warn("Failed to send keypress", e1);
        }
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        if (e.getKeyCode() <= 48) {
            handleSpecialChar(e);
        } else {
            handleChar(e);
        }

        e.consume();
    }

    private void handleChar(final KeyEvent e) {
        send(Charset.defaultCharset()
                .encode(Character.toString(e.getKeyChar())).array());
    }

    private void handleSpecialChar(final KeyEvent e) {
        switch(e.getKeyCode()) {
        case KeyEvent.VK_ENTER: send((byte) '\n'); break;
        case KeyEvent.VK_BACK_SPACE: send((byte) 8); break;
        case KeyEvent.VK_TAB: send((byte) 9); break;
        case KeyEvent.VK_ESCAPE: send((byte) 27); break;
        case KeyEvent.VK_SPACE: send((byte) 32); break;
        case KeyEvent.VK_DELETE: send((byte) 127); break; // TODO: map?
        case KeyEvent.VK_UP: send((byte) 27, (byte) '[', (byte) 'A'); break;
        case KeyEvent.VK_DOWN: send((byte) 27, (byte) '[', (byte) 'B'); break;
        case KeyEvent.VK_RIGHT: send((byte) 27, (byte) '[', (byte) 'C'); break;
        case KeyEvent.VK_LEFT: send((byte) 27, (byte) '[', (byte) 'D'); break;
        }
    }

    @Override
    public void keyReleased(final KeyEvent e) {
    }
}
