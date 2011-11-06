package de.jowisoftware.sshclient.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import de.jowisoftware.sshclient.terminal.buffer.Renderer;
import de.jowisoftware.sshclient.terminal.mouse.ClipboardManager;

public class AWTClipboard implements ClipboardManager, ClipboardOwner  {
    private static final long serialVersionUID = -7702866947155393382L;
    private final Renderer renderer;

    public AWTClipboard(final Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void copyPlaintext(final String string) {
        final StringSelection selection = new StringSelection(string);
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, this);
    }

    @Override
    public void lostOwnership(final Clipboard clipboard, final Transferable contents) {
        renderer.clearSelection();
    }
}
