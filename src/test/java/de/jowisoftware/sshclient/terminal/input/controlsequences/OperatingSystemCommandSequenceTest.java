package de.jowisoftware.sshclient.terminal.input.controlsequences;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

public class OperatingSystemCommandSequenceTest extends AbstractSequenceTest {
    private OperatingSystemCommandSequence sequence;

    @Before
    public void setUp() {
        sequence = new OperatingSystemCommandSequence();
    }

    @Test
    public void testMachers() {
        assertTrue(sequence.canHandleSequence("]0;test\u0007"));
        assertTrue(sequence.canHandleSequence("]0;x\u0007"));
        assertTrue(sequence.canHandleSequence("]1;y\u0007"));
        assertTrue(sequence.canHandleSequence("]2;x\u0007"));
        assertFalse(sequence.canHandleSequence("]0;x"));
        assertFalse(sequence.canHandleSequence("]3;x\u0007"));

        assertTrue(sequence.isPartialStart("]"));
        assertTrue(sequence.isPartialStart("]0"));
        assertTrue(sequence.isPartialStart("]0;"));
        assertTrue(sequence.isPartialStart("]0;test with words"));
        assertTrue(sequence.isPartialStart("]"));
        assertTrue(sequence.isPartialStart("]1"));
        assertTrue(sequence.isPartialStart("]2"));
        assertTrue(sequence.isPartialStart("]2;"));
        assertTrue(sequence.isPartialStart("]2;test with words"));

        assertFalse(sequence.isPartialStart("x"));
        assertFalse(sequence.isPartialStart("]3"));
        assertFalse(sequence.isPartialStart("]1x"));
    }

    @Test
    public void testSetTitle() {
        context.checking(new Expectations() {{
            oneOf(visualFeedback).newTitle("Title 1");
            oneOf(visualFeedback).newTitle("Another window title");
            oneOf(visualFeedback).newTitle("Title 2");
        }});

        sequence.handleSequence("]0;Title 1\u0007", sessionInfo);
        sequence.handleSequence("]0;Another window title\u0007", sessionInfo);
        sequence.handleSequence("]2;Title 2\u0007", sessionInfo);
    }
}
