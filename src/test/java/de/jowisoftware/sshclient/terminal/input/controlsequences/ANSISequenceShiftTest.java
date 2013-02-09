package de.jowisoftware.sshclient.terminal.input.controlsequences;

import org.jmock.Expectations;
import org.testng.annotations.Test;

public class ANSISequenceShiftTest extends AbstractSequenceTest {
    private void testLeftShiftXChars(final int charCount,
            final String ... args) {
        context.checking(new Expectations() {{
            oneOf(buffer).shift(charCount);
        }});

        DefaultSequenceRepository.executeAnsiSequence('P', sessionInfo, args);
    }

    private void testRightShiftXChars(final int charCount,
            final String ... args) {
        context.checking(new Expectations() {{
            oneOf(buffer).shift(-charCount);
        }});

        DefaultSequenceRepository.executeAnsiSequence('@', sessionInfo, args);
    }

    @Test
    public void leftShiftWithoutParameter() {
        testLeftShiftXChars(1);
    }

    @Test
    public void leftShift5Chars() {
        testLeftShiftXChars(5, "5");
    }

    @Test
    public void rightShiftWithoutParameter() {
        testRightShiftXChars(1);
    }

    @Test
    public void rightShift4Chars() {
        testRightShiftXChars(4, "4");
    }
}
