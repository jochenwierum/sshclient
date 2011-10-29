package de.jowisoftware.sshclient.terminal.controlsequences;

import java.util.List;

public interface SequenceRepository {
    List<NonASCIIControlSequence> getNonASCIISequences();
    ANSISequence getANSISequence(final char c);
}