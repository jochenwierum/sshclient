package de.jowisoftware.sshclient.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.events.KeyboardEvent;

public class KeyboardProcessor implements KeyListener, KeyboardEvent {
    private static final Logger LOGGER = Logger.getLogger(KeyboardProcessor.class);

    private static final char ESC = 27;
    private boolean cursorsInAppMode = false;
    private boolean numpadInAppMode;
    private SSHSession session;

    public void setSession(final SSHSession session) {
        this.session = session;
    }

    private void send(final char... value) {
        final byte bytes[] = new byte[value.length];
        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte) value[i];
        }
        session.sendToServer(bytes);
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        e.consume();

        if (session == null) {
            return;
        }
        session.getRenderer().clearSelection();

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
        session.sendToServer(Character.toString(e.getKeyChar()));
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
        final boolean isOnNumpad = e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD;
        boolean processed = false;

        switch (e.getKeyCode()) {
        case KeyEvent.VK_NUMPAD0:
            processed = sendNumpadNumber('0', 'p');
            break;
        case KeyEvent.VK_NUMPAD1:
            processed = sendNumpadNumber('1', 'q');
            break;
        case KeyEvent.VK_NUMPAD2:
            processed = sendNumpadNumber('2', 'r');
            break;
        case KeyEvent.VK_NUMPAD3:
            processed = sendNumpadNumber('3', 's');
            break;
        case KeyEvent.VK_NUMPAD4:
            processed = sendNumpadNumber('4', 't');
            break;
        case KeyEvent.VK_NUMPAD5:
            processed = sendNumpadNumber('5', 'u');
            break;
        case KeyEvent.VK_NUMPAD6:
            processed = sendNumpadNumber('6', 'v');
            break;
        case KeyEvent.VK_NUMPAD7:
            processed = sendNumpadNumber('7', 'w');
            break;
        case KeyEvent.VK_NUMPAD8:
            processed = sendNumpadNumber('8', 'x');
            break;
        case KeyEvent.VK_NUMPAD9:
            processed = sendNumpadNumber('9', 'y');
            break;
        case KeyEvent.VK_PLUS:
            processed = sendNumpadKey('+', 'M', isOnNumpad);
            break;
        case KeyEvent.VK_MINUS:
            processed = sendNumpadKey('-', 'm', isOnNumpad);
            break;
        case KeyEvent.VK_MULTIPLY:
            processed = sendNumpadKey('*', 'l', isOnNumpad);
            break;
        case KeyEvent.VK_DIVIDE:
            send('/');
            processed = true;
            break;
        case KeyEvent.VK_COMMA:
            processed = sendNumpadKey(',', 'n', isOnNumpad);
            break;
        }
        return processed;
    }

    private boolean sendNumpadKey(final char normalChar, final char appModeChar,
            final boolean isOnNumpad) {
        if (numpadInAppMode && isOnNumpad) {
            send(ESC, 'O', appModeChar);
        } else if (isOnNumpad) {
            send(normalChar);
        } else {
            return false;
        }
        return true;
    }

    private boolean sendNumpadNumber(final char normalChar, final char appModeChar) {
        if (numpadInAppMode) {
            send(ESC, 'O', appModeChar);
        } else {
            send(normalChar);
        }
        return true;
    }

    private boolean processMiscKeys(final KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_DELETE:
            send((char) 127);
            // TODO: read from config?
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
            send((char) 8);
            return true;
        case KeyEvent.VK_ESCAPE:
            send(ESC);
            return true;
        case KeyEvent.VK_TAB:
            if (e.isShiftDown()) {
                send(ESC, '[', 'z');
            } else {
                send((char) 9);
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

    @Override public void keyReleased(final KeyEvent e) { /* ignored */ }
    @Override public void keyTyped(final KeyEvent e) { /* ignored */ }

    @Override
    public void newCursorKeysIsAppMode(final boolean value) {
        cursorsInAppMode = value;
    }

    @Override
    public void newNumblockAppMode(final boolean b) {
        numpadInAppMode = b;
    }
}
