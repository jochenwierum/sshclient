package de.jowisoftware.sshclient.terminal.input.controlsequences;

import org.jmock.Expectations;
import org.junit.Test;

import de.jowisoftware.sshclient.terminal.input.controlsequences.DefaultSequenceRepository;


public class ANSISequenceRequestTest extends AbstractSequenceTest {
    private void prepareDeviceAttributes() {
        context.checking(new Expectations(){{
            oneOf(sessionInfo).sendToServer("\u001b[?6c");
        }});
    }

    @Test
    public void testDeviceAttributes1() {
        prepareDeviceAttributes();
        DefaultSequenceRepository.executeAnsiSequence('c', sessionInfo);
    }

    @Test
    public void testDeviceAttributes2() {
        prepareDeviceAttributes();
        DefaultSequenceRepository.executeAnsiSequence('c', sessionInfo, "0");
    }

    @Test
    public void testDeviceAttributes3() {
        prepareDeviceAttributes();
        DefaultSequenceRepository.executeAnsiSequence('c', sessionInfo, "1");
    }

    private void prepareSecondaryDeviceAttributes() {
        context.checking(new Expectations(){{
            oneOf(sessionInfo).sendToServer("\u001b[0;1;0c");
        }});
    }

    @Test
    public void testSecondaryDeviceAttributes1() {
        prepareSecondaryDeviceAttributes();
        DefaultSequenceRepository.executeAnsiSequence('c', sessionInfo, ">");
    }

    @Test
    public void testSecondaryDeviceAttributes2() {
        prepareSecondaryDeviceAttributes();
        DefaultSequenceRepository.executeAnsiSequence('c', sessionInfo, ">0");
    }

    @Test
    public void testSecondaryDeviceAttributes3() {
        prepareSecondaryDeviceAttributes();
        DefaultSequenceRepository.executeAnsiSequence('c', sessionInfo, ">1");
    }
}
