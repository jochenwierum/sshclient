package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.events.EventHub;
import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.TabStopManager;
import de.jowisoftware.sshclient.terminal.events.KeyboardEvent;
import de.jowisoftware.sshclient.terminal.events.VisualEvent;
import de.jowisoftware.sshclient.terminal.gfx.GfxCharSetup;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;

public abstract class AbstractSequenceTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    protected Buffer buffer;
    protected SSHSession sessionInfo;
    protected GfxCharSetup charSetup;
    protected KeyboardEvent keyboardFeedback;
    protected VisualEvent visualFeedback;
    protected TabStopManager tabstopManager;

    @Before
    public void setUpMocks() throws Exception {
        buffer = context.mock(Buffer.class);
        sessionInfo = context.mock(SSHSession.class);
        charSetup = context.mock(GfxCharSetup.class);
        keyboardFeedback = context.mock(KeyboardEvent.class);
        visualFeedback = context.mock(VisualEvent.class);
        tabstopManager = context.mock(TabStopManager.class);
        final EventHub<?> eventHubVF = context.mock(EventHub.class, "eventHubVF");
        final EventHub<?> eventHubKF = context.mock(EventHub.class, "eventHubKF");

        context.checking(new Expectations() {{
            allowing(sessionInfo).getBuffer(); will(returnValue(buffer));
            allowing(sessionInfo).getCharSetup(); will(returnValue(charSetup));
            allowing(sessionInfo).getKeyboardFeedback(); will(returnValue(eventHubKF));
            allowing(sessionInfo).getVisualFeedback(); will(returnValue(eventHubVF));
            allowing(sessionInfo).getTabStopManager(); will(returnValue(tabstopManager));
            allowing(eventHubKF).fire(); will(returnValue(keyboardFeedback));
            allowing(eventHubVF).fire(); will(returnValue(visualFeedback));
        }});
    }

}
