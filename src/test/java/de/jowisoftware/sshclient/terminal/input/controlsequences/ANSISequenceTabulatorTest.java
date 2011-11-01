package de.jowisoftware.sshclient.terminal.input.controlsequences;

import org.jmock.Expectations;
import org.junit.Test;

public class ANSISequenceTabulatorTest extends AbstractSequenceTest {
    @Test
    public void testRemoveTabstopWithoutParameter() {
        context.checking(new Expectations() {{
            oneOf(buffer).removeTabstopAtCurrentPosition();
        }});

        DefaultSequenceRepository.executeAnsiSequence('g', sessionInfo);
    }

    @Test
    public void testRemoveTabstopWithParameter() {
        context.checking(new Expectations() {{
            oneOf(buffer).removeTabstopAtCurrentPosition();
        }});

        DefaultSequenceRepository.executeAnsiSequence('g', sessionInfo, "0");
    }

    @Test
    public void testRemoveAllTabstops() {
        context.checking(new Expectations() {{
            oneOf(buffer).removeTabstops();
        }});

        DefaultSequenceRepository.executeAnsiSequence('g', sessionInfo, "3");
    }

    @Test
    public void testWrongParameterDoesNothing() {
        DefaultSequenceRepository.executeAnsiSequence('g', sessionInfo, "2");
    }
}
