package de.jowisoftware.sshclient.terminal.controlsequences;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.DisplayType;
import de.jowisoftware.sshclient.util.SequenceUtils;

@RunWith(JMock.class)
public class ANSISequenceHighLowTest extends AbstractSequenceTest {
    @Test
    public void testHandleNumblock() {
        context.checking(new Expectations() {{
            oneOf(keyboardFeedback).setCursorKeysIsAppMode(false);
        }});

        SequenceUtils.getANSISequence('l').process(sessionInfo, "?1");
    }

    @Test
    public void testHandleNumblockOff() {
        context.checking(new Expectations() {{
            oneOf(keyboardFeedback).setCursorKeysIsAppMode(true);
        }});

        SequenceUtils.getANSISequence('h').process(sessionInfo, "?1");
    }

    @Test
    public void testTerminalWidth132() {
        context.checking(new Expectations() {{
            oneOf(visualFeedback).setDisplayType(DisplayType.FIXED132X24);
        }});

        SequenceUtils.getANSISequence('h').process(sessionInfo, "?3");
    }

    @Test
    public void testTerminalWidth80() {
        context.checking(new Expectations() {{
            oneOf(visualFeedback).setDisplayType(DisplayType.FIXED80X24);
        }});

        SequenceUtils.getANSISequence('l').process(sessionInfo, "?3");
    }
}
