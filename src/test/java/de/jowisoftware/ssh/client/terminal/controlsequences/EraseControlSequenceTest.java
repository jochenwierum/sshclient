package de.jowisoftware.ssh.client.terminal.controlsequences;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.ssh.client.terminal.Buffer;
import de.jowisoftware.ssh.client.ui.GfxChar;

@RunWith(JMock.class)
public class EraseControlSequenceTest {
    private final Mockery context = new JUnit4Mockery();
    private EraseControlSequence<GfxChar> seq;
    private Buffer<GfxChar> buffer;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        buffer = context.mock(Buffer.class);
        seq = new EraseControlSequence<GfxChar>();
    }

    @Test
    public void testEraseCursorToBottom() {
        context.checking(new Expectations() {{
            oneOf(buffer).eraseToBottom();
        }});

        seq.handleSequence("[J", buffer, null, null);
    }

    @Test
    public void testEraseFromTop() {
        context.checking(new Expectations() {{
            oneOf(buffer).eraseFromTop();
        }});

        seq.handleSequence("[1J", buffer, null, null);
    }

    @Test
    public void testErase() {
        context.checking(new Expectations() {{
            oneOf(buffer).erase();
        }});

        seq.handleSequence("[2J", buffer, null, null);
    }

    @Test
    public void testEraseRestOfLine() {
        context.checking(new Expectations() {{
            oneOf(buffer).eraseRestOfLine();
        }});

        seq.handleSequence("[K", buffer, null, null);
    }

    @Test
    public void testEraseStartOfLine() {
        context.checking(new Expectations() {{
            oneOf(buffer).eraseStartOfLine();
        }});

        seq.handleSequence("[1K", buffer, null, null);
    }

    @Test
    public void testEraseLine() {
        context.checking(new Expectations() {{
            oneOf(buffer).eraseLine();
        }});

        seq.handleSequence("[2K", buffer, null, null);
    }

    @Test
    public void testCanHandle() {
        assertTrue(seq.canHandleSequence("[J"));
        assertTrue(seq.canHandleSequence("[1J"));
        assertTrue(seq.canHandleSequence("[2J"));
        assertTrue(seq.canHandleSequence("[K"));
        assertTrue(seq.canHandleSequence("[1K"));
        assertTrue(seq.canHandleSequence("[2K"));
        assertFalse(seq.canHandleSequence("[3K"));
        assertFalse(seq.canHandleSequence("[Y"));
        assertFalse(seq.canHandleSequence("["));
        assertTrue(seq.isPartialStart("["));
        assertTrue(seq.isPartialStart("[2"));
        assertFalse(seq.isPartialStart("[3"));
        assertFalse(seq.isPartialStart("X"));
    }
}
