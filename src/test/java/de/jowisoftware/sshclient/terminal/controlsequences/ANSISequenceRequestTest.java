package de.jowisoftware.sshclient.terminal.controlsequences;

import org.jmock.Expectations;
import org.junit.Test;

import de.jowisoftware.sshclient.util.SequenceUtils;

public class ANSISequenceRequestTest extends AbstractSequenceTest {
    @Test
    public void testVT100J() {
        context.checking(new Expectations(){{
            oneOf(sessionInfo).respond("\u001b[?1;2c");
            oneOf(sessionInfo).respond("\u001b[?1;2c");
        }});

        SequenceUtils.getANSISequence('c').process(sessionInfo);
        SequenceUtils.getANSISequence('c').process(sessionInfo, ">");
    }
}
