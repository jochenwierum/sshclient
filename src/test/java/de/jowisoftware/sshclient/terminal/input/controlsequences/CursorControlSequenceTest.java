package de.jowisoftware.sshclient.terminal.input.controlsequences;


import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CursorControlSequenceTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private CursorControlSequence seq;
    private Buffer buffer;
    private SSHSession sessionInfo;

    @Before
    public void setUp() throws Exception {
        buffer = context.mock(Buffer.class);
        seq = new CursorControlSequence();
        sessionInfo = context.mock(SSHSession.class);
        context.checking(new Expectations() {{
            allowing(sessionInfo).getBuffer(); will(returnValue(buffer));
        }});
    }

    @Test
    public void testMoveUpAndRoll() {
        context.checking(new Expectations() {{
            oneOf(buffer).moveCursorDown(false);
            oneOf(buffer).moveCursorDown(true);
        }});

        seq.handleSequence("D", sessionInfo);
        seq.handleSequence("E", sessionInfo);
    }

    @Test
    public void testMoveDownAndRoll() {
        context.checking(new Expectations() {{
            oneOf(buffer).moveCursorUp();
        }});

        seq.handleSequence("M", sessionInfo);
    }

    @Test public void
    esc7SavesCursor() {
        context.checking(new Expectations() {{
            oneOf(sessionInfo).saveState();
        }});

        seq.handleSequence("7", sessionInfo);
    }

    @Test public void
    esc8RestoresCursor() {
        context.checking(new Expectations() {{
            oneOf(sessionInfo).restoreState();
        }});

        seq.handleSequence("8", sessionInfo);
    }

    @Test
    public void testHandle() {
        assertTrue(seq.canHandleSequence("D"));
        assertTrue(seq.canHandleSequence("E"));
        assertTrue(seq.canHandleSequence("M"));
        assertTrue(seq.canHandleSequence("7"));
        assertTrue(seq.canHandleSequence("8"));
    }
}
