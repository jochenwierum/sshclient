package de.jowisoftware.sshclient.terminal.controlsequences;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.KeyboardFeedback;
import de.jowisoftware.sshclient.terminal.controlsequences.KeyboardControlSequence;
import de.jowisoftware.sshclient.ui.GfxChar;

@RunWith(JMock.class)
public class KeyboardControlSequenceTest {
    private final Mockery context = new JUnit4Mockery();
    private KeyboardControlSequence<GfxChar> sequence;

    @Before
    public void setUp() {
        sequence = new KeyboardControlSequence<GfxChar>();
    }

    @Test
    public void testMatches() {
        assertTrue(sequence.canHandleSequence("="));
        assertTrue(sequence.canHandleSequence(">"));
        assertTrue(sequence.canHandleSequence("[?1h"));
        assertTrue(sequence.canHandleSequence("[?1l"));
        assertFalse(sequence.canHandleSequence("[?12"));
        assertFalse(sequence.canHandleSequence("[X"));

        assertTrue(sequence.isPartialStart("["));
        assertTrue(sequence.isPartialStart("[?"));
        assertTrue(sequence.isPartialStart("[?1"));
        assertFalse(sequence.isPartialStart("[x"));
    }

    @Test
    public void testHandleAppKeys() {
        final KeyboardFeedback feedback = context.mock(KeyboardFeedback.class);

        context.checking(new Expectations() {{
            oneOf(feedback).setCursorKeysIsAppMode(true);
        }});

        sequence.handleSequence("[?1h", null, null, feedback);
    }

    @Test
    public void testHandleNonAppKeys() {
        final KeyboardFeedback feedback = context.mock(KeyboardFeedback.class);

        context.checking(new Expectations() {{
            oneOf(feedback).setCursorKeysIsAppMode(false);
        }});

        sequence.handleSequence("[?1l", null, null, feedback);
    }

    @Test
    public void testHandleNumblock() {
        final KeyboardFeedback feedback = context.mock(KeyboardFeedback.class);

        context.checking(new Expectations() {{
            oneOf(feedback).setNumblockIsAppMode(true);
        }});

        sequence.handleSequence("=", null, null, feedback);
    }

    @Test
    public void testHandleNumblockOff() {
        final KeyboardFeedback feedback = context.mock(KeyboardFeedback.class);

        context.checking(new Expectations() {{
            oneOf(feedback).setNumblockIsAppMode(false);
        }});

        sequence.handleSequence(">", null, null, feedback);
    }
}
