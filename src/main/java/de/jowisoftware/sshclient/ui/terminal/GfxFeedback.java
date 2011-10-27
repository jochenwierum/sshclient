package de.jowisoftware.sshclient.ui.terminal;

import de.jowisoftware.sshclient.terminal.DisplayType;
import de.jowisoftware.sshclient.terminal.VisualEvent;
import de.jowisoftware.sshclient.ui.SSHConsole;

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
