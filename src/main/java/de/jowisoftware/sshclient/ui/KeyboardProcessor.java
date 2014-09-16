package de.jowisoftware.sshclient.ui;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Renderer;
import de.jowisoftware.sshclient.terminal.events.KeyboardEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;

public class KeyboardProcessor implements KeyboardEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyboardProcessor.class);

    private static final byte ESC = 27;
    private boolean cursorsInAppMode = false;
    private boolean numpadInAppMode;
    private SSHSession session;

    public void setSession(final SSHSession session) {
        this.session = session;
    }

    private void send(final byte... value) {
        session.rawSendToServer(value);
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        e.consume();

        if (session == null) {
            return;
        }
        final Renderer renderer = session.getRenderer();
        renderer.resetBlinking();
        renderer.clearSelection();

        if (handleSpecialChar(e)) {
            return;
        }

        if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
            handleChar(e);
            return;
        }

        LOGGER.trace("Ignoring action key: {}", e.getKeyCode());
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
        boolean processed = true;

        switch (e.getKeyCode()) {
        case KeyEvent.VK_NUMPAD0:
            sendNumpadNumber((byte) '0', (byte) 'p');
            break;
        case KeyEvent.VK_NUMPAD1:
            sendNumpadNumber((byte) '1', (byte) 'q');
            break;
        case KeyEvent.VK_NUMPAD2:
            sendNumpadNumber((byte) '2', (byte) 'r');
            break;
        case KeyEvent.VK_NUMPAD3:
            sendNumpadNumber((byte) '3', (byte) 's');
            break;
        case KeyEvent.VK_NUMPAD4:
            sendNumpadNumber((byte) '4', (byte) 't');
            break;
        case KeyEvent.VK_NUMPAD5:
            sendNumpadNumber((byte) '5', (byte) 'u');
            break;
        case KeyEvent.VK_NUMPAD6:
            sendNumpadNumber((byte) '6', (byte) 'v');
            break;
        case KeyEvent.VK_NUMPAD7:
            sendNumpadNumber((byte) '7', (byte) 'w');
            break;
        case KeyEvent.VK_NUMPAD8:
            sendNumpadNumber((byte) '8', (byte) 'x');
            break;
        case KeyEvent.VK_NUMPAD9:
            sendNumpadNumber((byte) '9', (byte) 'y');
            break;
        case KeyEvent.VK_PLUS:
            processed = sendNumpadKey((byte) '+', (byte) 'M', isOnNumpad);
            break;
        case KeyEvent.VK_MINUS:
            processed = sendNumpadKey((byte) '-', (byte) 'm', isOnNumpad);
            break;
        case KeyEvent.VK_MULTIPLY:
            processed = sendNumpadKey((byte) '*', (byte) 'l', isOnNumpad);
            break;
        case KeyEvent.VK_DIVIDE:
            send((byte) '/');
            break;
        case KeyEvent.VK_COMMA:
            processed = sendNumpadKey((byte) ',', (byte) 'n', isOnNumpad);
            break;
        default:
            processed = false;
        }
        return processed;
    }

    private boolean sendNumpadKey(final byte normalChar, final byte appModeChar,
            final boolean isOnNumpad) {
        if (numpadInAppMode && isOnNumpad) {
            send(ESC, (byte) 'O', appModeChar);
        } else if (isOnNumpad) {
            send(normalChar);
        } else {
            return false;
        }
        return true;
    }

    private void sendNumpadNumber(final byte normalChar, final byte appModeChar) {
        if (numpadInAppMode) {
            send(ESC, (byte) 'O', appModeChar);
        } else {
            send(normalChar);
        }
    }

    private boolean processMiscKeys(final KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_DELETE:
            send((byte) 127);
            // TODO: read from config?
            // case KeyEvent.VK_DELETE: send(ESC, '[', '3', '~'); break;
            return true;
        case KeyEvent.VK_HOME:
            send(ESC, (byte) '[', (byte) '1', (byte) '~');
            return true;
        case KeyEvent.VK_END:
            send(ESC, (byte) '[', (byte) '4', (byte) '~');
            return true;
        case KeyEvent.VK_INSERT:
            send(ESC, (byte) '[', (byte) '2', (byte) '~');
            return true;
        case KeyEvent.VK_PAGE_UP:
            send(ESC, (byte) '[', (byte) '5', (byte) '~');
            return true;
        case KeyEvent.VK_PAGE_DOWN:
            send(ESC, (byte) '[', (byte) '6', (byte) '~');
            return true;
        case KeyEvent.VK_ENTER:
            send((byte) '\n');
            return true;
        case KeyEvent.VK_BACK_SPACE:
            send((byte) 8);
            return true;
        case KeyEvent.VK_ESCAPE:
            send(ESC);
            return true;
        case KeyEvent.VK_TAB:
            if (e.isShiftDown()) {
                send(ESC, (byte) '[', (byte) 'z');
            } else {
                send((byte) 9);
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
                send(ESC, (byte) 'O', (byte) 'A');
            } else {
                send(ESC, (byte) '[', (byte) 'A');
            }
            return true;
        case KeyEvent.VK_DOWN:
            if (cursorsInAppMode) {
                send(ESC, (byte) 'O', (byte) 'B');
            } else {
                send(ESC, (byte) '[', (byte) 'B');
            }
            return true;
        case KeyEvent.VK_RIGHT:
            if (cursorsInAppMode) {
                send(ESC, (byte) 'O', (byte) 'C');
            } else {
                send(ESC, (byte) '[', (byte) 'C');
            }
            return true;
        case KeyEvent.VK_LEFT:
            if (cursorsInAppMode) {
                send(ESC, (byte) 'O', (byte) 'D');
            } else {
                send(ESC, (byte) '[', (byte) 'D');
            }
            return true;
        default:
            return false;
        }
    }

    private boolean processFunctionKeys(final KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_F1:
            send(ESC, (byte) 'O', (byte) 'P');
            break;
        case KeyEvent.VK_F2:
            send(ESC, (byte) 'O', (byte) 'Q');
            break;
        case KeyEvent.VK_F3:
            send(ESC, (byte) 'O', (byte) 'R');
            break;
        case KeyEvent.VK_F4:
            send(ESC, (byte) 'O', (byte) 'S');
            break;
        case KeyEvent.VK_F5:
            send(ESC, (byte) '[', (byte) '1', (byte) '5', (byte) '~');
            break;
        case KeyEvent.VK_F6:
            send(ESC, (byte) '[', (byte) '1', (byte) '7', (byte) '~');
            break;
        case KeyEvent.VK_F7:
            send(ESC, (byte) '[', (byte) '1', (byte) '8', (byte) '~');
            break;
        case KeyEvent.VK_F8:
            send(ESC, (byte) '[', (byte) '1', (byte) '9', (byte) '~');
            break;
        case KeyEvent.VK_F9:
            send(ESC, (byte) '[', (byte) '2', (byte) '0', (byte) '~');
            break;
        case KeyEvent.VK_F10:
            send(ESC, (byte) '[', (byte) '2', (byte) '1', (byte) '~');
            break;
        case KeyEvent.VK_F11:
            send(ESC, (byte) '[', (byte) '2', (byte) '3', (byte) '~');
            break;
        case KeyEvent.VK_F12:
            send(ESC, (byte) '[', (byte) '2', (byte) '4', (byte) '~');
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
