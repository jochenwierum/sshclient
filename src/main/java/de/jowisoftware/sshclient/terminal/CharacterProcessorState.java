package de.jowisoftware.sshclient.terminal;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.controlsequences.NonASCIIControlSequence;

public class CharacterProcessorState<T extends GfxChar> {
    static enum State {
        BEGIN_SEQUENCE, ANSI_SEQUENCE, UNKNOWN_SEQUENCE;
    }

    public final List<NonASCIIControlSequence<T>> availableSequences =
        new LinkedList<NonASCIIControlSequence<T>>();

    public StringBuilder cachedChars = new StringBuilder();
    public State state = State.BEGIN_SEQUENCE;

    public CharacterProcessorState(final Collection<NonASCIIControlSequence<T>> sequences) {
        availableSequences.addAll(sequences);
    }

    public String getCachedString() {
        return cachedChars.toString();
    }
}
