package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.terminal.SSHSession;

public interface ANSISequence {
    void process(SSHSession sessionInfo, String... args);
}
