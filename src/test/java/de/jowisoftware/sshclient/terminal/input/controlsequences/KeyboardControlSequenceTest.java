package de.jowisoftware.sshclient.terminal.input.controlsequences;

import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class KeyboardControlSequenceTest extends AbstractSequenceTest {
    private KeyboardControlSequence sequence;

    @Before
    public void setUp() {
        sequence = new KeyboardControlSequence();
    }

    @Test
    public void testMatches() {
        assertTrue(sequence.canHandleSequence("="));
        assertTrue(sequence.canHandleSequence(">"));
    }

    @Test
    public void testHandleNumblock() {
        context.checking(new Expectations() {{
            oneOf(keyboardFeedback).newNumblockAppMode(true);
        }});

        sequence.handleSequence("=", sessionInfo);
    }

    @Test
    public void testHandleNumblockOff() {
        context.checking(new Expectations() {{
            oneOf(keyboardFeedback).newNumblockAppMode(false);
        }});

        sequence.handleSequence(">", sessionInfo);
    }
}
