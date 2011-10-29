package de.jowisoftware.sshclient.terminal.input.controlsequences;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.events.EventHub;
import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.events.KeyboardEvent;
import de.jowisoftware.sshclient.terminal.events.VisualEvent;
import de.jowisoftware.sshclient.terminal.gfx.GfxCharSetup;

@RunWith(JMock.class)
public abstract class AbstractSequenceTest {
    protected final Mockery context = new JUnit4Mockery();
    protected Buffer buffer;
    protected SSHSession sessionInfo;
    protected GfxCharSetup charSetup;
    protected KeyboardEvent keyboardFeedback;
    protected VisualEvent visualFeedback;

    @Before
    public void setUp() throws Exception {
        buffer = context.mock(Buffer.class);
        sessionInfo = context.mock(SSHSession.class);
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
