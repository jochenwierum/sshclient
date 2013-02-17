package de.jowisoftware.sshclient.terminal.input;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.jowisoftware.sshclient.terminal.input.controlsequences.NonASCIIControlSequence;


public class CharacterProcessorState {
    static enum State {
        BEGIN_SEQUENCE, ANSI_SEQUENCE, UNKNOWN_SEQUENCE
    }

    public final List<NonASCIIControlSequence> availableSequences =
        new LinkedList<>();

    public final StringBuilder cachedChars = new StringBuilder();
    public State state = State.BEGIN_SEQUENCE;

    public CharacterProcessorState(final Collection<NonASCIIControlSequence> sequences) {
        availableSequences.addAll(sequences);
    }

    public String getCachedString() {
        return cachedChars.toString();
    }
}
