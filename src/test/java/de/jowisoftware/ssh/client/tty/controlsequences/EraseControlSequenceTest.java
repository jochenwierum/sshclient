package de.jowisoftware.ssh.client.tty.controlsequences;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.ssh.client.tty.Buffer;
import de.jowisoftware.ssh.client.ui.GfxChar;

@RunWith(JMock.class)
public class EraseControlSequenceTest {
    private final Mockery context = new Mockery();
    private EraseControlSequence<GfxChar> seq;
    private Buffer<GfxChar> buffer;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        buffer = context.mock(Buffer.class);
        seq = new EraseControlSequence<GfxChar>();
    }

    @Test
    public void testErase() {
        context.checking(new Expectations() {{
            oneOf(buffer).eraseDown();
        }});

        seq.handleSequence("[J", buffer, null);
    }

    @Test
    public void testHandle() {
        assertTrue(seq.canHandleSequence("[J"));
        assertFalse(seq.canHandleSequence("[K"));
        assertFalse(seq.canHandleSequence("["));
        assertTrue(seq.isPartialStart("["));
        assertFalse(seq.isPartialStart("X"));
    }
}
