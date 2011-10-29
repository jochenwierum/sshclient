package de.jowisoftware.sshclient.ui;

import de.jowisoftware.sshclient.terminal.DisplayType;
import de.jowisoftware.sshclient.terminal.VisualEvent;

public class GfxFeedback implements VisualEvent {
    private final SSHConsole parent;

    public GfxFeedback(final SSHConsole parent) {
        this.parent = parent;
    }

    @Override
    public void bell() {
        System.err.println("BELL!");
    }

    @Override
    public void newTitle(final String title) { /* ignored */ }

    @Override
    public void setDisplayType(final DisplayType displayType) {
        parent.setDisplayType(displayType);
    }
}
