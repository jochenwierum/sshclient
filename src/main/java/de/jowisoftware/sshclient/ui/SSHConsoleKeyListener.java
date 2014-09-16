package de.jowisoftware.sshclient.ui;

import de.jowisoftware.sshclient.terminal.SSHSession;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SSHConsoleKeyListener implements KeyListener {
    private final SSHSession session;
    private final SSHConsoleHistory history;

    public SSHConsoleKeyListener(final SSHSession session, final SSHConsoleHistory history) {
        this.session = session;
        this.history = history;
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        if (!handleScrollEvent(e)) {
            session.getKeyboardFeedback().fire().keyPressed(e);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean handleScrollEvent(final KeyEvent e) {
        if (e.isShiftDown()) {
            switch(e.getKeyCode()) {
            case KeyEvent.VK_PAGE_DOWN:
                history.scrollPageDown();
                return true;
            case KeyEvent.VK_PAGE_UP:
                history.scrollPageUp();
                return true;
            case KeyEvent.VK_DOWN:
                history.scrollDown();
                return true;
            case KeyEvent.VK_UP:
                history.scrollUp();
                return true;
            default:
                return false;
            }
        }
        return false;
    }

    @Override public void keyTyped(final KeyEvent e) { /* ignored */ }
    @Override public void keyReleased(final KeyEvent e) { /* ignored */ }
}
