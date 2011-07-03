package de.jowisoftware.sshclient.ui;

import de.jowisoftware.sshclient.terminal.VisualFeedback;

public class GfxFeedback implements VisualFeedback {
    @Override
    public void bell() {
        System.err.println("BELL!");
    }

    @Override
    public void setTitle(final String title) {
        System.out.println("Title: " + title);
    }
}
