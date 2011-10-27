package de.jowisoftware.sshclient.terminal.controlsequences;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.events.EventHub;
import de.jowisoftware.sshclient.terminal.GfxCharSetup;
import de.jowisoftware.sshclient.terminal.KeyboardEvent;
import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.VisualEvent;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

@RunWith(JMock.class)
public abstract class AbstractSequenceTest {
    protected final Mockery context = new JUnit4Mockery();
    protected Buffer<GfxChar> buffer;
    protected Session<GfxChar> sessionInfo;
    protected GfxCharSetup<GfxChar> charSetup;
    protected KeyboardEvent keyboardFeedback;
    protected VisualEvent visualFeedback;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        buffer = context.mock(Buffer.class);
        sessionInfo = context.mock(Session.class);
        charSetup = context.mock(GfxCharSetup.class);
        keyboardFeedback = context.mock(KeyboardEvent.class);
        visualFeedback = context.mock(VisualEvent.class);
        final EventHub<?> eventHubVF = context.mock(EventHub.class, "eventHubVF");
        final EventHub<?> eventHubKF = context.mock(EventHub.class, "eventHubKF");

        context.checking(new Expectations() {{
            allowing(sessionInfo).getBuffer(); will(returnValue(buffer));
            allowing(sessionInfo).getCharSetup(); will(returnValue(charSetup));
            allowing(sessionInfo).getKeyboardFeedback(); will(returnValue(eventHubKF));
            allowing(sessionInfo).getVisualFeedback(); will(returnValue(eventHubVF));
            allowing(eventHubKF).fire(); will(returnValue(keyboardFeedback));
            allowing(eventHubVF).fire(); will(returnValue(visualFeedback));
        }});
    }

}
