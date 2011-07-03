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

import de.jowisoftware.sshclient.terminal.SessionInfo;
import de.jowisoftware.sshclient.terminal.VisualFeedback;
import de.jowisoftware.sshclient.ui.GfxChar;

@RunWith(JMock.class)
public class OperatingSystemCommandSequenceTest {
    private final Mockery context = new JUnit4Mockery();
    private OperatingSystemCommandSequence<GfxChar> sequence;
    private SessionInfo<GfxChar> sessionInfo;
    private VisualFeedback visualFeedback;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        sequence = new OperatingSystemCommandSequence<GfxChar>();
        sessionInfo = context.mock(SessionInfo.class);
        visualFeedback = context.mock(VisualFeedback.class);
        context.checking(new Expectations() {{
            allowing(sessionInfo).getVisualFeedback(); will(returnValue(visualFeedback));
        }});
    }

    @Test
    public void testMachers() {
        assertTrue(sequence.canHandleSequence("]0;test\u0007"));
        assertTrue(sequence.canHandleSequence("]0;x\u0007"));
        assertTrue(sequence.canHandleSequence("]2;x\u0007"));
        assertFalse(sequence.canHandleSequence("]0;x"));
        assertFalse(sequence.canHandleSequence("]1;x\u0007"));

        assertTrue(sequence.isPartialStart("]"));
        assertTrue(sequence.isPartialStart("]0"));
        assertTrue(sequence.isPartialStart("]0;"));
        assertTrue(sequence.isPartialStart("]0;test with words"));
        assertTrue(sequence.isPartialStart("]"));
        assertTrue(sequence.isPartialStart("]2"));
        assertTrue(sequence.isPartialStart("]2;"));
        assertTrue(sequence.isPartialStart("]2;test with words"));

        assertFalse(sequence.isPartialStart("x"));
        assertFalse(sequence.isPartialStart("]1"));
        assertFalse(sequence.isPartialStart("]1x"));
    }

    @Test
    public void testSetTitle() {
        context.checking(new Expectations() {{
            oneOf(visualFeedback).setTitle("Title 1");
            oneOf(visualFeedback).setTitle("Another window title");
            oneOf(visualFeedback).setTitle("Title 2");
        }});

        sequence.handleSequence("]0;Title 1\u0007", sessionInfo);
        sequence.handleSequence("]0;Another window title\u0007", sessionInfo);
        sequence.handleSequence("]2;Title 2\u0007", sessionInfo);
    }
}
