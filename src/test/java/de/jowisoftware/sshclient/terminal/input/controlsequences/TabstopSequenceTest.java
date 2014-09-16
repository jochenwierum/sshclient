package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.terminal.buffer.Position;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TabstopSequenceTest extends AbstractSequenceTest {
    private NonASCIIControlSequence sequence;

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
    public void handleSequenceSetsNewTabAtColumn3() {
        assertNewTab(3);
    }

    @Test
    public void handleSequenceSetsNewTabAtColumn9() {
        assertNewTab(9);
    }

    private void assertNewTab(final int x) {
        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(x, 7)));
            oneOf(tabstopManager).addTab(x);
        }});

        sequence.handleSequence("H", sessionInfo);
    }
}
