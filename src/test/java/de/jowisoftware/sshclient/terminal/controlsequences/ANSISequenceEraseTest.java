package de.jowisoftware.sshclient.terminal.controlsequences;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.util.SequenceUtils;

@RunWith(JMock.class)
public class ANSISequenceEraseTest extends AbstractSequenceTest {

    @Test
    public void testEraseCursorToBottom() {
        context.checking(new Expectations() {{
            oneOf(buffer).eraseToBottom();
        }});

        SequenceUtils.getANSISequence('J').process(sessionInfo);
    }

    @Test
    public void testEraseFromTop() {
        context.checking(new Expectations() {{
            oneOf(buffer).eraseFromTop();
        }});

        SequenceUtils.getANSISequence('J').process(sessionInfo, "1");
    }

    @Test
    public void testErase() {
        context.checking(new Expectations() {{
            oneOf(buffer).erase();
        }});

        SequenceUtils.getANSISequence('J').process(sessionInfo, "2");
    }

    @Test
    public void testEraseRestOfLine() {
        context.checking(new Expectations() {{
            oneOf(buffer).eraseRestOfLine();
        }});

        SequenceUtils.getANSISequence('K').process(sessionInfo);
    }

    @Test
    public void testEraseStartOfLine() {
        context.checking(new Expectations() {{
            oneOf(buffer).eraseStartOfLine();
        }});

        SequenceUtils.getANSISequence('K').process(sessionInfo, "1");
    }

    @Test
    public void testEraseLine() {
        context.checking(new Expectations() {{
            oneOf(buffer).eraseLine();
        }});

        SequenceUtils.getANSISequence('K').process(sessionInfo, "2");
    }
}
