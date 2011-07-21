package de.jowisoftware.sshclient.terminal.controlsequences;

import java.util.List;

import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

public interface SequenceRepository<T extends GfxChar> {
    List<NonASCIIControlSequence<T>> getNonASCIISequences();
    ANSISequence<T> getANSISequence(final char c);
}