package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.ui.GfxChar;

public interface ANSISequence<T extends GfxChar> {
    public void process(Session<T> sessionInfo, String... args);
}
