package de.jowisoftware.sshclient.terminal.controlsequences;

import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.KeyboardFeedback;
import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.ui.GfxChar;

@RunWith(JMock.class)
public class KeyboardControlSequenceTest {
    private final Mockery context = new JUnit4Mockery();
    private KeyboardControlSequence<GfxChar> sequence;
    private KeyboardFeedback feedback;
    private Session<GfxChar> sessionInfo;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        sequence = new KeyboardControlSequence<GfxChar>();
        feedback = context.mock(KeyboardFeedback.class);
        sessionInfo = context.mock(Session.class);
        context.checking(new Expectations() {{
            allowing(sessionInfo).getKeyboardFeedback(); will(returnValue(feedback));
        }});
    }

    @Test
    public void testMatches() {
        assertTrue(sequence.canHandleSequence("="));
        assertTrue(sequence.canHandleSequence(">"));
    }

    @Test
    public void testHandleNumblock() {
        context.checking(new Expectations() {{
            oneOf(feedback).setNumblockIsAppMode(true);
        }});

        sequence.handleSequence("=", sessionInfo);
    }

    @Test
    public void testHandleNumblockOff() {
        context.checking(new Expectations() {{
            oneOf(feedback).setNumblockIsAppMode(false);
        }});

        sequence.handleSequence(">", sessionInfo);
    }
}
