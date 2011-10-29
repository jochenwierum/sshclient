package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Session;

public interface ANSISequence {
    void process(Session sessionInfo, String... args);
}
