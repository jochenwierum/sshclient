package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.SessionInfo;
import de.jowisoftware.sshclient.ui.GfxChar;

public interface ANSISequence<T extends GfxChar> {
    public void process(SessionInfo<T> sessionInfo, String... args);
}
