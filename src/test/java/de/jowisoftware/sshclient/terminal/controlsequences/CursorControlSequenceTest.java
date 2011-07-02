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

import de.jowisoftware.sshclient.terminal.Buffer;
import de.jowisoftware.sshclient.terminal.CursorPosition;
import de.jowisoftware.sshclient.terminal.controlsequences.CursorControlSequence;
import de.jowisoftware.sshclient.ui.GfxChar;

@RunWith(JMock.class)
public class CursorControlSequenceTest {
    private final Mockery context = new JUnit4Mockery();
    private CursorControlSequence<GfxChar> seq;
    private Buffer<GfxChar> buffer;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        buffer = context.mock(Buffer.class);
        seq = new CursorControlSequence<GfxChar>();
    }

    @Test
    public void testHomePosition() {
        context.checking(new Expectations() {{
            oneOf(buffer).setAbsoluteCursorPosition(new CursorPosition(1, 1));
        }});

        seq.handleSequence("[H", buffer, null, null);
    }

    @Test
    public void testCustomPositions() {
        context.checking(new Expectations() {{
            oneOf(buffer).setCursorPosition(new CursorPosition(5, 1));
            oneOf(buffer).setCursorPosition(new CursorPosition(7, 3));
        }});

        seq.handleSequence("[1;5H", buffer, null, null);
        seq.handleSequence("[3;7H", buffer, null, null);
    }

    @Test
    public void testSetupRoll() {
        context.checking(new Expectations() {{
            oneOf(buffer).setRollRange(1, 5);
            oneOf(buffer).setCursorPosition(new CursorPosition(1, 1));
            oneOf(buffer).setRollRange(3, 7);
            oneOf(buffer).setCursorPosition(new CursorPosition(1, 1));
        }});

        seq.handleSequence("[1;5r", buffer, null, null);
        seq.handleSequence("[3;7r", buffer, null, null);
    }

    @Test
    public void testRemoveRoll() {
        context.checking(new Expectations() {{
            oneOf(buffer).deleteRollRange();
            oneOf(buffer).setCursorPosition(new CursorPosition(1, 1));
        }});

        seq.handleSequence("[r", buffer, null, null);
    }

    @Test
    public void testHandle() {
        assertTrue(seq.canHandleSequence("[H"));
        assertTrue(seq.canHandleSequence("[r"));
        assertTrue(seq.canHandleSequence("[1;2H"));
        assertTrue(seq.canHandleSequence("[12;23H"));
        assertTrue(seq.canHandleSequence("[12;23r"));
        assertFalse(seq.canHandleSequence("[4H"));
        assertFalse(seq.canHandleSequence("[2;H"));
        assertFalse(seq.canHandleSequence("[X"));
        assertTrue(seq.canHandleSequence("[5A"));
        assertTrue(seq.canHandleSequence("[A"));
        assertTrue(seq.canHandleSequence("[25B"));
        assertTrue(seq.canHandleSequence("[B"));
        assertTrue(seq.canHandleSequence("[6C"));
        assertTrue(seq.canHandleSequence("[C"));
        assertTrue(seq.canHandleSequence("[98D"));
        assertTrue(seq.canHandleSequence("[D"));

        assertTrue(seq.isPartialStart("["));
        assertTrue(seq.isPartialStart("[1"));
        assertTrue(seq.isPartialStart("[32;"));
        assertTrue(seq.isPartialStart("[33;7"));
        assertFalse(seq.isPartialStart("[1;2;"));
        assertFalse(seq.isPartialStart("X"));
    }
}
