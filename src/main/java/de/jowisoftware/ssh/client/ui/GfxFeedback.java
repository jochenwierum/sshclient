package de.jowisoftware.ssh.client.ui;

import de.jowisoftware.ssh.client.terminal.VisualFeedback;

public class GfxFeedback implements VisualFeedback {
    @Override
    public void bell() {
        System.err.println("BELL!");
    }
}
