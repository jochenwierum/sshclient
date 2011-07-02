package de.jowisoftware.ssh.client.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;

import de.jowisoftware.ssh.client.terminal.KeyboardFeedback;
import de.jowisoftware.ssh.client.util.StringUtils;

public class KeyboardProcessor implements KeyListener, KeyboardFeedback {
    private static final Logger LOGGER = Logger.getLogger(SSHConsole.class);
    private OutputStream responseStream;

    private static final int ESC = 27;
    public boolean cursorsInAppMode = false;
    private boolean numpadInAppMode;

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
        for (final int c : value) {
            send((byte) c);
        }
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        e.consume();

        if (responseStream == null) {
            return;
        }

        if (handleSpecialChar(e)) {
            return;
        }

        if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
            handleChar(e);
            return;
        }

        LOGGER.trace("Ignoring action key: " + e.getKeyCode() + " ("
                + e.getKeyChar() + ")");
    }

    private void handleChar(final KeyEvent e) {
        send(Charset.defaultCharset()
                .encode(Character.toString(e.getKeyChar())).array());
    }

    private boolean handleSpecialChar(final KeyEvent e) {
        return
            processCursorKeys(e) ||
            processMiscKeys(e) ||
            processFunctionKeys(e) ||
            processNumblock(e)
            ;
    }

    private boolean processNumblock(final KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_NUMPAD0:
            if (numpadInAppMode) {
                send(ESC, 'O', 'p');
            } else {
                send('0');
            }
            return true;
        case KeyEvent.VK_NUMPAD1:
            if (numpadInAppMode) {
                send(ESC, 'O', 'q');
            } else {
                send('1');
            }
            return true;
        case KeyEvent.VK_NUMPAD2:
            if (numpadInAppMode) {
                send(ESC, 'O', 'r');
            } else {
                send('2');
            }
            return true;
        case KeyEvent.VK_NUMPAD3:
            if (numpadInAppMode) {
                send(ESC, 'O', 's');
            } else {
                send('3');
            }
            return true;
        case KeyEvent.VK_NUMPAD4:
            if (numpadInAppMode) {
                send(ESC, 'O', 't');
            } else {
                send('4');
            }
            return true;
        case KeyEvent.VK_NUMPAD5:
            if (numpadInAppMode) {
                send(ESC, 'O', 'u');
            } else {
                send('5');
            }
            return true;
        case KeyEvent.VK_NUMPAD6:
            if (numpadInAppMode) {
                send(ESC, 'O', 'v');
            } else {
                send('6');
            }
            return true;
        case KeyEvent.VK_NUMPAD7:
            if (numpadInAppMode) {
                send(ESC, 'O', 'w');
            } else {
                send('7');
            }
            return true;
        case KeyEvent.VK_NUMPAD8:
            if (numpadInAppMode) {
                send(ESC, 'O', 'x');
            } else {
                send('8');
            }
            return true;
        case KeyEvent.VK_NUMPAD9:
            if (numpadInAppMode) {
                send(ESC, 'O', 'y');
            } else {
                send('9');
            }
            return true;
        case KeyEvent.VK_PLUS:
            if (numpadInAppMode && e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
                send(ESC, 'O', 'M');
            } else {
                send('+');
            }
            return true;
        case KeyEvent.VK_MINUS:
            if (numpadInAppMode && e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
                send(ESC, 'O', 'm');
            } else {
                send('-');
            }
            return true;
        case KeyEvent.VK_MULTIPLY:
            if (numpadInAppMode && e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
                send(ESC, 'O', 'l');
            } else {
                send('*');
            }
            return true;
        case KeyEvent.VK_DIVIDE:
            send('/');
            return true;
        case KeyEvent.VK_COMMA:
            if (numpadInAppMode && e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
                send(ESC, 'O', 'n');
            } else {
                send(',');
            }
            return true;
        default:
            return false;
        }
    }

    private boolean processMiscKeys(final KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_DELETE:
            send(127);
            // TODO: map?
            // case KeyEvent.VK_DELETE: send(ESC, '[', '3', '~'); break;
            return true;
        case KeyEvent.VK_HOME:
            send(ESC, '[', '1', '~');
            return true;
        case KeyEvent.VK_END:
            send(ESC, '[', '4', '~');
            return true;
        case KeyEvent.VK_INSERT:
            send(ESC, '[', '2', '~');
            return true;
        case KeyEvent.VK_PAGE_UP:
            send(ESC, '[', '5', '~');
            return true;
        case KeyEvent.VK_PAGE_DOWN:
            send(ESC, '[', '6', '~');
            return true;
        case KeyEvent.VK_ENTER:
            send('\n');
            return true;
        case KeyEvent.VK_BACK_SPACE:
            send(8);
            return true;
        case KeyEvent.VK_ESCAPE:
            send(ESC);
            return true;
        case KeyEvent.VK_TAB:
            if (e.isShiftDown()) {
                send(ESC, '[', 'z');
            } else {
                send(9);
            }
            return true;
        default:
            return false;
        }
    }

    private boolean processCursorKeys(final KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_UP:
            if (cursorsInAppMode) {
                send(ESC, 'O', 'A');
            } else {
                send(ESC, '[', 'A');
            }
            return true;
        case KeyEvent.VK_DOWN:
            if (cursorsInAppMode) {
                send(ESC, 'O', 'B');
            } else {
                send(ESC, '[', 'B');
            }
            return true;
        case KeyEvent.VK_RIGHT:
            if (cursorsInAppMode) {
                send(ESC, 'O', 'C');
            } else {
                send(ESC, '[', 'C');
            }
            return true;
        case KeyEvent.VK_LEFT:
            if (cursorsInAppMode) {
                send(ESC, 'O', 'D');
            } else {
                send(ESC, '[', 'D');
            }
            return true;
        default:
            return false;
        }
    }

    private boolean processFunctionKeys(final KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_F1:
            send(ESC, 'O', 'p');
            break;
        case KeyEvent.VK_F2:
            send(ESC, 'O', 'q');
            break;
        case KeyEvent.VK_F3:
            send(ESC, 'O', 'r');
            break;
        case KeyEvent.VK_F4:
            send(ESC, 'O', 's');
            break;
        case KeyEvent.VK_F5:
            send(ESC, 'O', 't');
            break;
        case KeyEvent.VK_F6:
            send(ESC, '1', '7', '~');
            break;
        case KeyEvent.VK_F7:
            send(ESC, '1', '8', '~');
            break;
        case KeyEvent.VK_F8:
            send(ESC, '1', '9', '~');
            break;
        case KeyEvent.VK_F9:
            send(ESC, '2', '0', '~');
            break;
        case KeyEvent.VK_F10:
            send(ESC, '2', '1', '~');
            break;
        case KeyEvent.VK_F11:
            send(ESC, '2', '3', '~');
            break;
        case KeyEvent.VK_F12:
            send(ESC, '2', '4', '~');
            break;
        default:
            return false;
        }
        return true;
    }

    @Override public void keyReleased(final KeyEvent e) { }
    @Override public void keyTyped(final KeyEvent e) { }

    @Override
    public void setCursorKeysIsAppMode(final boolean value) {
        cursorsInAppMode = value;
    }

    public void setOutputStream(final OutputStream outputStream) {
        this.responseStream = outputStream;
    }

    @Override
    public void setNumblockIsAppMode(final boolean b) {
        numpadInAppMode = b;
    }
}
