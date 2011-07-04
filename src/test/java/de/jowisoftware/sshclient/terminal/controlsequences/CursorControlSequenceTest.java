package de.jowisoftware.sshclient.terminal.controlsequences;


import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.Buffer;
import de.jowisoftware.sshclient.terminal.SessionInfo;
import de.jowisoftware.sshclient.ui.GfxChar;

@RunWith(JMock.class)
public class CursorControlSequenceTest {
    private final Mockery context = new JUnit4Mockery();
    private CursorControlSequence<GfxChar> seq;
    private Buffer<GfxChar> buffer;
    private SessionInfo<GfxChar> sessionInfo;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        buffer = context.mock(Buffer.class);
        seq = new CursorControlSequence<GfxChar>();
        sessionInfo = context.mock(SessionInfo.class);
        context.checking(new Expectations() {{
            allowing(sessionInfo).getBuffer(); will(returnValue(buffer));
        }});
    }

    @Test
    public void testMoveUpAndRoll() {
        context.checking(new Expectations() {{
            oneOf(buffer).moveCursorDownAndRoll(false);
            oneOf(buffer).moveCursorDownAndRoll(true);
        }});

        seq.handleSequence("D", sessionInfo);
        seq.handleSequence("E", sessionInfo);
    }

    @Test
    public void testMoveDownAndRoll() {
        context.checking(new Expectations() {{
            oneOf(buffer).moveCursorUpAndRoll();
        }});

        seq.handleSequence("M", sessionInfo);
    }

    @Test
    public void testHandle() {
        assertTrue(seq.canHandleSequence("D"));
        assertTrue(seq.canHandleSequence("E"));
        assertTrue(seq.canHandleSequence("M"));
    }
}
