package de.jowisoftware.sshclient.ui;

import de.jowisoftware.sshclient.debug.PerformanceLogger;
import de.jowisoftware.sshclient.debug.PerformanceType;
import de.jowisoftware.sshclient.terminal.SimpleSSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.mouse.MouseCursorManager;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

public class SSHConsoleMouseListener implements MouseInputListener {
    private final JComponent parent;
    private final MouseCursorManager mouseCursorManager;
    private final SimpleSSHSession session;
    private final AWTClipboard clipboard;

    public SSHConsoleMouseListener(final JPanel parent,
            final MouseCursorManager mouseCursorManager, final SimpleSSHSession session,
            final AWTClipboard clipboard) {
        this.parent = parent;
        this.mouseCursorManager = mouseCursorManager;
        this.session = session;
        this.clipboard = clipboard;
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            parent.requestFocusInWindow();

            final Position charPosition =
                    session.translateMousePositionToCharacterPosition(e.getX(), e.getY());
            mouseCursorManager.startSelection(charPosition, e.getClickCount());
            mouseCursorManager.updateSelectionEnd(charPosition);
            session.render();
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseCursorManager.copySelection();
            updateSelection(e);
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            clipboard.pasteToServer();
        }
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
            PerformanceLogger.start(PerformanceType.SELECT_TO_RENDER);
            updateSelection(e);
        }
    }

    public void updateSelection(final MouseEvent e) {
        final Position charPosition =
                session.translateMousePositionToCharacterPosition(e.getX(), e.getY());
        mouseCursorManager.updateSelectionEnd(charPosition);
        session.render();
    }

    @Override public void mouseEntered(final MouseEvent e) { /* ignored */ }
    @Override public void mouseExited(final MouseEvent e) { /* ignored */ }
    @Override public void mouseMoved(final MouseEvent e) { /* ignored */ }
    @Override public void mouseClicked(final MouseEvent e) { /* ignored */ }
}
