package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Session;

public interface NonASCIIControlSequence {
    boolean isPartialStart(String sequence);
    boolean canHandleSequence(String sequence);
    void handleSequence(String sequence, Session sessionInfo);
}
