package de.jowisoftware.sshclient.terminal.input.controlsequences;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

public class TabstopSequenceTest extends AbstractSequenceTest {
    public NonASCIIControlSequence sequence;

    @Before
    public void setUp() {
        sequence = new TabstopSequence();
    }

    @Test
    public void canHandleEscapeSequence() {
        assertTrue(sequence.canHandleSequence("H"));
        assertFalse(sequence.canHandleSequence("h"));
        assertFalse(sequence.canHandleSequence("7"));
    }

    @Test
    public void handleSequenceSetsNewTab() {
        context.checking(new Expectations() {{
            oneOf(buffer).addTabstopToCurrentPosition();
        }});

        sequence.handleSequence("H", sessionInfo);
    }
}
