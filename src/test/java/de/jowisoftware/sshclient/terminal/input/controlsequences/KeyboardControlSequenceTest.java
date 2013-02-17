package de.jowisoftware.sshclient.terminal.input.controlsequences;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

public class KeyboardControlSequenceTest extends AbstractSequenceTest {
    private KeyboardControlSequence sequence;

    @Before
    public void setUp() {
        sequence = new KeyboardControlSequence();
    }

    @Test
    public void testMatches() {
        assertThat(sequence.canHandleSequence("="), is(true));
        assertThat(sequence.canHandleSequence(">"), is(true));
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
