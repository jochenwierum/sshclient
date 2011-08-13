package de.jowisoftware.sshclient.terminal.controlsequences;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.Attribute;
import de.jowisoftware.sshclient.terminal.TerminalColor;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

@RunWith(JMock.class)
public class ANSISequenceAttributeTest extends AbstractSequenceTest {
    private void callWithAttrAndExpect(final int attr, final Attribute expect) {
        context.checking(new Expectations() {{
            oneOf(charSetup).setAttribute(expect);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, Integer.toString(attr));
    }

    private void callWithAttrAndExpectFGColor(final int attr, final TerminalColor expect) {
        context.checking(new Expectations() {{
            oneOf(charSetup).setForeground(expect);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, Integer.toString(attr));
    }

    private void callWithAttrAndExpectBGColor(final int attr, final TerminalColor expect) {
        final GfxChar gfxChar = context.mock(GfxChar.class, "color-" + attr);

        context.checking(new Expectations() {{
            oneOf(charSetup).setBackground(expect);
            oneOf(charSetup).createClearChar(); will(returnValue(gfxChar));
            oneOf(buffer).setClearChar(gfxChar);
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
        callWithAttrAndExpectFGColor(30, TerminalColor.BLACK);
        callWithAttrAndExpectFGColor(31, TerminalColor.RED);
        callWithAttrAndExpectFGColor(32, TerminalColor.GREEN);
        callWithAttrAndExpectFGColor(33, TerminalColor.YELLOW);
        callWithAttrAndExpectFGColor(34, TerminalColor.BLUE);
        callWithAttrAndExpectFGColor(35, TerminalColor.MAGENTA);
        callWithAttrAndExpectFGColor(36, TerminalColor.CYAN);
        callWithAttrAndExpectFGColor(37, TerminalColor.WHITE);
        callWithAttrAndExpectFGColor(39, TerminalColor.DEFAULT);
    }

    @Test
    public void testBackgroundColors() {
        callWithAttrAndExpectBGColor(40, TerminalColor.BLACK);
        callWithAttrAndExpectBGColor(41, TerminalColor.RED);
        callWithAttrAndExpectBGColor(42, TerminalColor.GREEN);
        callWithAttrAndExpectBGColor(43, TerminalColor.YELLOW);
        callWithAttrAndExpectBGColor(44, TerminalColor.BLUE);
        callWithAttrAndExpectBGColor(45, TerminalColor.MAGENTA);
        callWithAttrAndExpectBGColor(46, TerminalColor.CYAN);
        callWithAttrAndExpectBGColor(47, TerminalColor.WHITE);
        callWithAttrAndExpectBGColor(49, TerminalColor.DEFAULTBG);
    }

    @Test
    public void resetAttributes() {
        final GfxChar gfxChar = context.mock(GfxChar.class, "color");

        context.checking(new Expectations() {{
            oneOf(charSetup).reset();
            oneOf(charSetup).createClearChar(); will(returnValue(gfxChar));
            oneOf(buffer).setClearChar(gfxChar);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, "0");
    }

    @Test
    public void defaultAttributes() {
        final GfxChar gfxChar = context.mock(GfxChar.class, "color");

        context.checking(new Expectations() {{
            oneOf(charSetup).reset();
            oneOf(charSetup).createClearChar(); will(returnValue(gfxChar));
            oneOf(buffer).setClearChar(gfxChar);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo);
    }

    @Test
    public void testMultipleAttributes() {
        final GfxChar gfxChar = context.mock(GfxChar.class, "color");
        final Sequence sequence = context.sequence("seq");

        context.checking(new Expectations() {{
            oneOf(charSetup).reset(); inSequence(sequence);
            oneOf(charSetup).createClearChar(); will(returnValue(gfxChar)); inSequence(sequence);
            oneOf(buffer).setClearChar(gfxChar); inSequence(sequence);

            oneOf(charSetup).setAttribute(Attribute.BLINK); inSequence(sequence);
            oneOf(charSetup).setForeground(TerminalColor.BLUE); inSequence(sequence);
            oneOf(charSetup).setBackground(TerminalColor.RED); inSequence(sequence);
            oneOf(charSetup).createClearChar(); will(returnValue(gfxChar)); inSequence(sequence);
            oneOf(buffer).setClearChar(gfxChar); inSequence(sequence);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, "0", "5", "34", "41");
    }
}
