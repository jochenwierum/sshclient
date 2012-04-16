package de.jowisoftware.sshclient.terminal.input.controlsequences;

import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.List;

import org.hamcrest.Matcher;
import org.junit.Test;

public class DefaultSequenceRepositoryTest {
    @Test
    public void invalidSequenceDoesNotReturnNull() {
        assertThat(new DefaultSequenceRepository().getANSISequence('?'),
                isA(ANSISequence.class));
    }

    // TODO: fix this warnings!
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void defaultRepositoryContiansAllCommandSequences() {
        final List<NonASCIIControlSequence> sequences = new DefaultSequenceRepository().getNonASCIISequences();
        assertThat(sequences, (Matcher) hasItem(isA(CursorControlSequence.class)));
        assertThat(sequences, (Matcher) hasItem(isA(CursorControlSequence.class)));
        assertThat(sequences, (Matcher) hasItem(isA(KeyboardControlSequence.class)));
        assertThat(sequences, (Matcher) hasItem(isA(OperatingSystemCommandSequence.class)));
        assertThat(sequences, (Matcher) hasItem(isA(DebugControlSequence.class)));
        assertThat(sequences, (Matcher) hasItem(isA(CharsetControlSequence.class)));
        assertThat(sequences, (Matcher) hasItem(isA(ColorCommandSequence.class)));
        assertThat(sequences, (Matcher) hasItem(isA(TabstopSequence.class)));
    }
}
