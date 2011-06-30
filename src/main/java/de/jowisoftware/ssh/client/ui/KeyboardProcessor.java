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

    private static final int ESC = 27;

    public KeyboardProcessor(final OutputStream outputStream) {
        this.responseStream = outputStream;
    }

    @Override
    public void keyTyped(final KeyEvent e) {
    }

    private void send(final byte[] value) {
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
            LOGGER.warn("Failed to send keypress: " + value, e1);
        }
    }

    private void send(final int... value) {
        for (final int c: value) {
            send((byte) c);
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
        case KeyEvent.VK_ENTER: send('\n'); break;
        case KeyEvent.VK_BACK_SPACE: send(8); break;
        case KeyEvent.VK_ESCAPE: send(ESC); break;
        case KeyEvent.VK_SPACE: send(32); break;
        case KeyEvent.VK_DELETE: send(127); break; // TODO: map?
        //case KeyEvent.VK_DELETE: send(ESC, '[', '3', '~'); break;
        case KeyEvent.VK_UP: send(ESC, '[', 'A'); break;
        case KeyEvent.VK_DOWN: send(ESC, '[', 'B'); break;
        case KeyEvent.VK_RIGHT: send(ESC, '[', 'C'); break;
        case KeyEvent.VK_LEFT: send(ESC, '[', 'D'); break;
        case KeyEvent.VK_HOME: send(ESC, '[', 1, '~'); break;
        case KeyEvent.VK_END: send(ESC, '[', '4', '~'); break;
        case KeyEvent.VK_INSERT: send(ESC, '[', '2', '~'); break;
        case KeyEvent.VK_PAGE_UP: send(ESC, '[', '5', '~'); break;
        case KeyEvent.VK_PAGE_DOWN: send(ESC, '[', '6', '~'); break;
        case KeyEvent.VK_F1: send(ESC, 'O', 'p'); break;
        case KeyEvent.VK_F2: send(ESC, 'O', 'q'); break;
        case KeyEvent.VK_F3: send(ESC, 'O', 'r'); break;
        case KeyEvent.VK_F4: send(ESC, 'O', 's'); break;
        case KeyEvent.VK_F5: send(ESC, 'O', 't'); break;
        case KeyEvent.VK_F6: send(ESC, '1', '7', '~'); break;
        case KeyEvent.VK_F7: send(ESC, '1', '8', '~'); break;
        case KeyEvent.VK_F8: send(ESC, '1', '9', '~'); break;
        case KeyEvent.VK_F9: send(ESC, '2', '0', '~'); break;
        case KeyEvent.VK_F10: send(ESC, '2', '1', '~'); break;
        case KeyEvent.VK_F11: send(ESC, '2', '3', '~'); break;
        case KeyEvent.VK_F12: send(ESC, '2', '4', '~'); break;
        case KeyEvent.VK_TAB:
            if (e.isShiftDown()) {
                send(ESC, '[', 'z');
            } else {
                send(9);
            }
            break;
        }
    }

    @Override
    public void keyReleased(final KeyEvent e) {
    }
}
