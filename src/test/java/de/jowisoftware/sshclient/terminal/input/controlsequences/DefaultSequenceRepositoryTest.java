package de.jowisoftware.sshclient.terminal.input.controlsequences;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.isA;

import java.util.List;

import org.junit.Test;

public class DefaultSequenceRepositoryTest {
    @Test
    public void invalidSequenceDoesNotReturnNull() {
        assertThat(new DefaultSequenceRepository().getANSISequence('?'),
                isA(ANSISequence.class));
    }

    @Test
    public void defaultRepositoryContiansAllCommandSequences() {
        final List<NonASCIIControlSequence> sequences = new DefaultSequenceRepository().getNonASCIISequences();
        assertThat(sequences, hasItem(isA(CursorControlSequence.class)));
        assertThat(sequences, hasItem(isA(CursorControlSequence.class)));
        assertThat(sequences, hasItem(isA(KeyboardControlSequence.class)));
        assertThat(sequences, hasItem(isA(OperatingSystemCommandSequence.class)));
        assertThat(sequences, hasItem(isA(DebugControlSequence.class)));
        assertThat(sequences, hasItem(isA(CharsetControlSequence.class)));
        assertThat(sequences, hasItem(isA(ColorCommandSequence.class)));
        assertThat(sequences, hasItem(isA(TabstopSequence.class)));
    }
}
