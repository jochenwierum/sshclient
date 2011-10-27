package de.jowisoftware.sshclient.terminal.controlsequences;

import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.events.EventHub;
import de.jowisoftware.sshclient.terminal.KeyboardEvent;
import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

@RunWith(JMock.class)
public class KeyboardControlSequenceTest {
    private final Mockery context = new JUnit4Mockery();
    private KeyboardControlSequence<GfxChar> sequence;
    private KeyboardEvent feedback;
    private Session<GfxChar> sessionInfo;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        sequence = new KeyboardControlSequence<GfxChar>();
        feedback = context.mock(KeyboardEvent.class);
        sessionInfo = context.mock(Session.class);
        final EventHub<?> eventHub = context.mock(EventHub.class);
        context.checking(new Expectations() {{
            allowing(sessionInfo).getKeyboardFeedback(); will(returnValue(eventHub));
            allowing(eventHub).fire(); will(returnValue(feedback));
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
            oneOf(feedback).newNumblockAppMode(true);
        }});

        sequence.handleSequence("=", sessionInfo);
    }

    @Test
    public void testHandleNumblockOff() {
        context.checking(new Expectations() {{
            oneOf(feedback).newNumblockAppMode(false);
        }});

        sequence.handleSequence(">", sessionInfo);
    }
}
