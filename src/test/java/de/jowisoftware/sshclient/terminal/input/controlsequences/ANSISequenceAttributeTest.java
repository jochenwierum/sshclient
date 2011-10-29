package de.jowisoftware.sshclient.terminal.input.controlsequences;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.gfx.Attribute;
import de.jowisoftware.sshclient.terminal.gfx.ColorFactory;
import de.jowisoftware.sshclient.terminal.gfx.TerminalColor;
import de.jowisoftware.sshclient.terminal.input.controlsequences.DefaultSequenceRepository;

@RunWith(JMock.class)
public class ANSISequenceAttributeTest extends AbstractSequenceTest {
    private ColorFactory colorFactory;

    @Before
    public void setUpColorFactory() {
        colorFactory = context.mock(ColorFactory.class);
        context.checking(new Expectations(){{
            allowing(charSetup).getColorFactory(); will(returnValue(colorFactory));
        }});
    }

    private void callWithAttrAndExpect(final int attr, final Attribute expect) {
        context.checking(new Expectations() {{
            oneOf(charSetup).setAttribute(expect);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, Integer.toString(attr));
    }

    private void callWithAttrAndExpectFGColor(final int attr) {
        final TerminalColor color = context.mock(TerminalColor.class, "TerminalColor" + attr);

        context.checking(new Expectations() {{
            oneOf(colorFactory).createStandardColor(attr); will(returnValue(color));
            oneOf(color).isForeground(); will(returnValue(true));
            oneOf(charSetup).setForeground(color);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, Integer.toString(attr));
    }

    private void callWithAttrAndExpectBGColor(final int attr) {
        final TerminalColor color = context.mock(TerminalColor.class, "TerminalColor" + attr);
        final GfxChar gfxChar = context.mock(GfxChar.class, "gfxChar" + attr);

        context.checking(new Expectations() {{
            oneOf(colorFactory).createStandardColor(attr);
                will(returnValue(color));
            oneOf(color).isForeground(); will(returnValue(false));
            oneOf(charSetup).setBackground(color);
            oneOf(charSetup).createClearChar();
                will(returnValue(gfxChar));
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
        callWithAttrAndExpectFGColor(30);
        callWithAttrAndExpectFGColor(31);
    }

    @Test
    public void testBackgroundColors() {
        callWithAttrAndExpectBGColor(40);
        callWithAttrAndExpectBGColor(41);
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

        final TerminalColor color1 = context.mock(TerminalColor.class, "color1");
        final TerminalColor color2 = context.mock(TerminalColor.class, "color2");

        context.checking(new Expectations() {{
            oneOf(charSetup).reset(); inSequence(sequence);
            oneOf(charSetup).createClearChar(); will(returnValue(gfxChar)); inSequence(sequence);
            oneOf(buffer).setClearChar(gfxChar); inSequence(sequence);

            oneOf(colorFactory).createStandardColor(34); will(returnValue(color1));
            oneOf(colorFactory).createStandardColor(41); will(returnValue(color2));

            oneOf(charSetup).setAttribute(Attribute.BLINK); inSequence(sequence);
            oneOf(color1).isForeground(); will(returnValue(true));
            oneOf(charSetup).setForeground(color1); inSequence(sequence);
            oneOf(color2).isForeground(); will(returnValue(false));
            oneOf(charSetup).setBackground(color2); inSequence(sequence);
            oneOf(charSetup).createClearChar(); will(returnValue(gfxChar)); inSequence(sequence);
            oneOf(buffer).setClearChar(gfxChar); inSequence(sequence);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, "0", "5", "34", "41");
    }


}
