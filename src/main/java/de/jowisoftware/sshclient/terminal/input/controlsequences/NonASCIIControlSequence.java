package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.terminal.SSHSession;

public interface NonASCIIControlSequence {
    boolean isPartialStart(String sequence);
    boolean canHandleSequence(String sequence);
    void handleSequence(String sequence, SSHSession sessionInfo);
}
