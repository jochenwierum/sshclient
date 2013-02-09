package de.jowisoftware.sshclient.terminal.input.controlsequences;

import static org.testng.Assert.assertTrue;

import org.jmock.Expectations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class KeyboardControlSequenceTest extends AbstractSequenceTest {
    private KeyboardControlSequence sequence;

    @BeforeMethod
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
