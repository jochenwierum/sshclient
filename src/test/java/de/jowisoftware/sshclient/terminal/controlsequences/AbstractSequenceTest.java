package de.jowisoftware.sshclient.terminal.controlsequences;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.Buffer;
import de.jowisoftware.sshclient.terminal.GfxCharSetup;
import de.jowisoftware.sshclient.terminal.KeyboardFeedback;
import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.VisualFeedback;
import de.jowisoftware.sshclient.ui.GfxChar;

@RunWith(JMock.class)
public abstract class AbstractSequenceTest {
    protected final Mockery context = new JUnit4Mockery();
    protected Buffer<GfxChar> buffer;
    protected Session<GfxChar> sessionInfo;
    protected GfxCharSetup<GfxChar> charSetup;
    protected KeyboardFeedback keyboardFeedback;
    protected VisualFeedback visualFeedback;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        buffer = context.mock(Buffer.class);
        sessionInfo = context.mock(Session.class);
        charSetup = context.mock(GfxCharSetup.class);
        keyboardFeedback = context.mock(KeyboardFeedback.class);
        visualFeedback = context.mock(VisualFeedback.class);

        context.checking(new Expectations() {{
            allowing(sessionInfo).getBuffer(); will(returnValue(buffer));
            allowing(sessionInfo).getCharSetup(); will(returnValue(charSetup));
            allowing(sessionInfo).getKeyboardFeedback(); will(returnValue(keyboardFeedback));
            allowing(sessionInfo).getVisualFeedback(); will(returnValue(visualFeedback));
        }});
    }

}
