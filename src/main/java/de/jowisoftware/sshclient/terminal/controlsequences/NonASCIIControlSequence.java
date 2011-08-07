package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

public interface NonASCIIControlSequence<T extends GfxChar> {
    boolean isPartialStart(String sequence);
    boolean canHandleSequence(String sequence);
    void handleSequence(String sequence, Session<T> sessionInfo);
}
