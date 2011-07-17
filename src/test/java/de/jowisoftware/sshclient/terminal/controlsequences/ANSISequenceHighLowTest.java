package de.jowisoftware.sshclient.terminal.controlsequences;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.DisplayType;
import de.jowisoftware.sshclient.terminal.Position;
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
            final Position size = new Position(132, 24);
            oneOf(visualFeedback).setDisplayType(DisplayType.FIXED132X24);
            oneOf(buffer).getSize(); will(returnValue(size));
            oneOf(buffer).erase(size.toRange());
            oneOf(buffer).setCursorPosition(new Position(1, 1));
        }});

        SequenceUtils.getANSISequence('h').process(sessionInfo, "?3");
    }

    @Test
    public void testTerminalWidth80() {
        context.checking(new Expectations() {{
            final Position size = new Position(80, 24);
            oneOf(visualFeedback).setDisplayType(DisplayType.FIXED80X24);
            oneOf(buffer).getSize(); will(returnValue(size));
            oneOf(buffer).erase(size.toRange());
            oneOf(buffer).setCursorPosition(new Position(1, 1));
        }});

        SequenceUtils.getANSISequence('l').process(sessionInfo, "?3");
    }

    @Test
    public void testOriginModeHigh() {
        context.checking(new Expectations() {{
            oneOf(buffer).setCursorRelativeToMargin(true);
            oneOf(buffer).setCursorPosition(new Position(1, 1));
        }});

        SequenceUtils.getANSISequence('h').process(sessionInfo, "?6");
    }

    @Test
    public void testOriginModeLow() {
        context.checking(new Expectations() {{
            oneOf(buffer).setCursorRelativeToMargin(false);
            oneOf(buffer).setCursorPosition(new Position(1, 1));
        }});

        SequenceUtils.getANSISequence('l').process(sessionInfo, "?6");
    }
}
