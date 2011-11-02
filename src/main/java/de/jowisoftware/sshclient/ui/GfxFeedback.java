package de.jowisoftware.sshclient.ui;

import de.jowisoftware.sshclient.terminal.buffer.Renderer;
import de.jowisoftware.sshclient.terminal.events.DisplayType;
import de.jowisoftware.sshclient.terminal.events.VisualEvent;

public class GfxFeedback implements VisualEvent {
    private final SSHConsole parent;
    private final Renderer renderer;

    public GfxFeedback(final SSHConsole parent,
            final Renderer renderer) {
        this.parent = parent;
        this.renderer = renderer;
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

    @Override
    public void newInverseMode(final boolean active) {
        renderer.renderInverted(active);
    }
}
