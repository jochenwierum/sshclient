package de.jowisoftware.sshclient.terminal.controlsequences;

import org.jmock.Expectations;
import org.junit.Test;

import de.jowisoftware.sshclient.util.SequenceUtils;

public class ANSISequenceRequestTest extends AbstractSequenceTest {
    private void prepareDeviceAttributes() {
        context.checking(new Expectations(){{
            oneOf(sessionInfo).sendToServer("\u001b[?6c");
        }});
    }

    @Test
    public void testDeviceAttributes1() {
        prepareDeviceAttributes();
        SequenceUtils.getANSISequence('c').process(sessionInfo);
    }

    @Test
    public void testDeviceAttributes2() {
        prepareDeviceAttributes();
        SequenceUtils.getANSISequence('c').process(sessionInfo, "0");
    }

    @Test
    public void testDeviceAttributes3() {
        prepareDeviceAttributes();
        SequenceUtils.getANSISequence('c').process(sessionInfo, "1");
    }

    private void prepareSecondaryDeviceAttributes() {
        context.checking(new Expectations(){{
            oneOf(sessionInfo).sendToServer("\u001b[0;1;0c");
        }});
    }

    @Test
    public void testSecondaryDeviceAttributes1() {
        prepareSecondaryDeviceAttributes();
        SequenceUtils.getANSISequence('c').process(sessionInfo, ">");
    }

    @Test
    public void testSecondaryDeviceAttributes2() {
        prepareSecondaryDeviceAttributes();
        SequenceUtils.getANSISequence('c').process(sessionInfo, ">0");
    }

    @Test
    public void testSecondaryDeviceAttributes3() {
        prepareSecondaryDeviceAttributes();
        SequenceUtils.getANSISequence('c').process(sessionInfo, ">1");
    }
}
