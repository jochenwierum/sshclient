package de.jowisoftware.sshclient.terminal.controlsequences;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.Attribute;
import de.jowisoftware.sshclient.terminal.Color;

@RunWith(JMock.class)
public class ANSISequenceAttributeTest extends AbstractSequenceTest {
    private void callWithAttrAndExpect(final int attr, final Attribute expect) {
        context.checking(new Expectations() {{
            oneOf(charSetup).setAttribute(expect);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, Integer.toString(attr));
    }

    private void callWithAttrAndExpectFGColor(final int attr, final Color expect) {
        context.checking(new Expectations() {{
            oneOf(charSetup).setForeground(expect);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, Integer.toString(attr));
    }

    private void callWithAttrAndExpectBGColor(final int attr, final Color expect) {
        context.checking(new Expectations() {{
            oneOf(charSetup).setBackground(expect);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, Integer.toString(attr));
    }

    private void callWithRemoveAttrAndExpect(final int attr, final Attribute expect) {
        context.checking(new Expectations() {{
            oneOf(charSetup).removeAttribute(expect);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, Integer.toString(attr));
    }

    @Test
    public void testAttributes1to8() {
        callWithAttrAndExpect(1, Attribute.BRIGHT);
        callWithAttrAndExpect(2, Attribute.DIM);
        callWithAttrAndExpect(4, Attribute.UNDERSCORE);
        callWithAttrAndExpect(5, Attribute.BLINK);
        callWithAttrAndExpect(7, Attribute.INVERSE);
        callWithAttrAndExpect(8, Attribute.HIDDEN);
    }

    @Test
    public void testAttributes22to28() {
        callWithRemoveAttrAndExpect(22, Attribute.BRIGHT);
        callWithRemoveAttrAndExpect(23, Attribute.DIM); // is this correct?
        callWithRemoveAttrAndExpect(24, Attribute.UNDERSCORE);
        callWithRemoveAttrAndExpect(25, Attribute.BLINK);
        callWithRemoveAttrAndExpect(27, Attribute.INVERSE);
        callWithRemoveAttrAndExpect(28, Attribute.HIDDEN);
    }

    @Test
    public void testForegroundColors() {
        callWithAttrAndExpectFGColor(30, Color.BLACK);
        callWithAttrAndExpectFGColor(31, Color.RED);
        callWithAttrAndExpectFGColor(32, Color.GREEN);
        callWithAttrAndExpectFGColor(33, Color.YELLOW);
        callWithAttrAndExpectFGColor(34, Color.BLUE);
        callWithAttrAndExpectFGColor(35, Color.MAGENTA);
        callWithAttrAndExpectFGColor(36, Color.CYAN);
        callWithAttrAndExpectFGColor(37, Color.WHITE);
        callWithAttrAndExpectFGColor(38, Color.DEFAULT);
    }

    @Test
    public void testBackgroundColors() {
        callWithAttrAndExpectBGColor(40, Color.BLACK);
        callWithAttrAndExpectBGColor(41, Color.RED);
        callWithAttrAndExpectBGColor(42, Color.GREEN);
        callWithAttrAndExpectBGColor(43, Color.YELLOW);
        callWithAttrAndExpectBGColor(44, Color.BLUE);
        callWithAttrAndExpectBGColor(45, Color.MAGENTA);
        callWithAttrAndExpectBGColor(46, Color.CYAN);
        callWithAttrAndExpectBGColor(47, Color.WHITE);
        callWithAttrAndExpectBGColor(48, Color.DEFAULTBG);
    }

    @Test
    public void testResetAttributes() {
        context.checking(new Expectations() {{
            oneOf(charSetup).reset();
            oneOf(charSetup).reset();
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, "0");
        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo);
    }

    @Test
    public void testMultipleAttributes() {
        context.checking(new Expectations() {{
            oneOf(charSetup).reset();
            oneOf(charSetup).setBackground(Color.RED);
            oneOf(charSetup).setForeground(Color.BLUE);
            oneOf(charSetup).setAttribute(Attribute.BLINK);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, "0", "5", "34", "41");
    }
}
