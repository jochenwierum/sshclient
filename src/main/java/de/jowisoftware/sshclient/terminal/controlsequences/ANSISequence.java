package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

public interface ANSISequence<T extends GfxChar> {
    void process(Session<T> sessionInfo, String... args);
}
