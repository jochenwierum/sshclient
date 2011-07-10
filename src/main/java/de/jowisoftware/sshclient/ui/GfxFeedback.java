package de.jowisoftware.sshclient.ui;

import de.jowisoftware.sshclient.terminal.DisplayType;
import de.jowisoftware.sshclient.terminal.VisualFeedback;

public class GfxFeedback implements VisualFeedback {
    private final SSHConsole parent;

    public GfxFeedback(final SSHConsole parent) {
        this.parent = parent;
    }

    @Override
    public void bell() {
        System.err.println("BELL!");
    }

    @Override
    public void setTitle(final String title) {
        System.out.println("Title: " + title);
    }

    @Override
    public void setDisplayType(final DisplayType displayType) {
        parent.setDisplayType(displayType);
    }
}
