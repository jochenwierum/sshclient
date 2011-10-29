package de.jowisoftware.sshclient.terminal.input.controlsequences;

import static de.jowisoftware.sshclient.HamcrestHelper.containsElementThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DefaultSequenceRepositoryTest {
    @Test
    public void invalidSequenceDoesNotReturnNull() {
        assertThat(new DefaultSequenceRepository().getANSISequence('?'),
                is(ANSISequence.class));
    }

    @Test
    public void defaultRepositoryContiansAllCommandSequences() {
        assertThat(new DefaultSequenceRepository().getNonASCIISequences(),
                containsElementThat(is(CursorControlSequence.class)));
        assertThat(new DefaultSequenceRepository().getNonASCIISequences(),
                containsElementThat(is(KeyboardControlSequence.class)));
        assertThat(new DefaultSequenceRepository().getNonASCIISequences(),
                containsElementThat(is(OperatingSystemCommandSequence.class)));
        assertThat(new DefaultSequenceRepository().getNonASCIISequences(),
                containsElementThat(is(DebugControlSequence.class)));
        assertThat(new DefaultSequenceRepository().getNonASCIISequences(),
                containsElementThat(is(CharsetControlSequence.class)));
        assertThat(new DefaultSequenceRepository().getNonASCIISequences(),
                containsElementThat(is(ColorCommandSequence.class)));
    }
}
