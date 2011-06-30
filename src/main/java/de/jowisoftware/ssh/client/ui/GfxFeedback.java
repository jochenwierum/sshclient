package de.jowisoftware.ssh.client.ui;

import de.jowisoftware.ssh.client.terminal.Feedback;

public class GfxFeedback implements Feedback {
    @Override
    public void bell() {
        System.err.println("BELL!");
    }
}
