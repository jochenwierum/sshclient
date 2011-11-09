package de.jowisoftware.sshclient.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.mouse.ClipboardManager;

public class AWTClipboard implements ClipboardManager, ClipboardOwner  {
    private static final Logger LOGGER = Logger.getLogger(AWTClipboard.class);

    private final SSHSession session;

    public AWTClipboard(final SSHSession session) {
        this.session = session;
    }

    @Override
    public void copyPlaintext(final String string) {
        final StringSelection selection = new StringSelection(string);
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, this);
    }

    @Override
    public void lostOwnership(final Clipboard clipboard, final Transferable contents) {
        session.getRenderer().clearSelection();
    }

    public void pasteToServer() {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        final Transferable contents = clipboard.getContents(this);

        if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                final String text = (String) contents.getTransferData(DataFlavor.stringFlavor);
                session.sendToServer(text);
            } catch (final UnsupportedFlavorException e) {
                LOGGER.warn("Could not paste clipboard", e);
            } catch (final IOException e) {
                LOGGER.warn("Could not paste clipboard", e);
            }
        }
    }
}
