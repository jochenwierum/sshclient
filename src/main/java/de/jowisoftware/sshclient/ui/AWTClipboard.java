package de.jowisoftware.sshclient.ui;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.mouse.ClipboardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public class AWTClipboard implements ClipboardManager, ClipboardOwner  {
    private static final Logger LOGGER = LoggerFactory.getLogger(AWTClipboard.class);

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
            } catch (final UnsupportedFlavorException | IOException e) {
                LOGGER.warn("Could not paste clipboard", e);
            }
        }
    }
}
