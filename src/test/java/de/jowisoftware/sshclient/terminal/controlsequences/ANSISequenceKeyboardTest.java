package de.jowisoftware.sshclient.terminal.controlsequences;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.util.SequenceUtils;

@RunWith(JMock.class)
public class ANSISequenceKeyboardTest extends AbstractSequenceTest {
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
}
