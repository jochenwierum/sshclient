package de.jowisoftware.sshclient.terminal.input.controlsequences;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.gfx.Attribute;
import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.terminal.gfx.GfxChar;

@RunWith(JMock.class)
public class ANSISequenceAttributeTest extends AbstractSequenceTest {
    private void callWithAttrAndExpect(final int attr, final Attribute expect) {
        context.checking(new Expectations() {{
            oneOf(charSetup).setAttribute(expect);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, Integer.toString(attr));
    }

    private void callWithAttrAndExpectFGColor(final int attr) {
        context.checking(new Expectations() {{
            oneOf(charSetup).setForeground(ColorName.find(attr));
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, Integer.toString(attr));
    }

    private void callWithAttrAndExpectBGColor(final int attr) {
        final GfxChar gfxChar = context.mock(GfxChar.class, "clearChar-" + attr);

        context.checking(new Expectations() {{
            oneOf(charSetup).setBackground(ColorName.find(attr));
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
    public void canSetAttributes1to8() {
        callWithAttrAndExpect(1, Attribute.BRIGHT);
        callWithAttrAndExpect(2, Attribute.DIM);
        callWithAttrAndExpect(4, Attribute.UNDERSCORE);
        callWithAttrAndExpect(5, Attribute.BLINK);
        callWithAttrAndExpect(7, Attribute.INVERSE);
        callWithAttrAndExpect(8, Attribute.HIDDEN);
    }

    @Test
    public void canSetAttributes22to28() {
        callWithRemoveAttrAndExpect(22, Attribute.BRIGHT);
        callWithRemoveAttrAndExpect(23, Attribute.DIM); // is this correct?
        callWithRemoveAttrAndExpect(24, Attribute.UNDERSCORE);
        callWithRemoveAttrAndExpect(25, Attribute.BLINK);
        callWithRemoveAttrAndExpect(27, Attribute.INVERSE);
        callWithRemoveAttrAndExpect(28, Attribute.HIDDEN);
    }

    @Test
    public void canSetForegroundColors() {
        callWithAttrAndExpectFGColor(30);
        callWithAttrAndExpectFGColor(31);
    }

    @Test
    public void canSetBackgroundColors() {
        callWithAttrAndExpectBGColor(40);
        callWithAttrAndExpectBGColor(41);
    }

    @Test
    public void canResetAttributesByProviding0() {
        final GfxChar gfxChar = context.mock(GfxChar.class, "color");

        context.checking(new Expectations() {{
            oneOf(charSetup).reset();
            oneOf(charSetup).createClearChar(); will(returnValue(gfxChar));
            oneOf(buffer).setClearChar(gfxChar);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, "0");
    }

    @Test
    public void canResetAttributesByProvidingNothing() {
        final GfxChar gfxChar = context.mock(GfxChar.class, "color");

        context.checking(new Expectations() {{
            oneOf(charSetup).reset();
            oneOf(charSetup).createClearChar(); will(returnValue(gfxChar));
            oneOf(buffer).setClearChar(gfxChar);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo);
    }

    @Test
    public void canHandleMultipleAttributes() {
        final GfxChar gfxChar = context.mock(GfxChar.class, "color");
        final Sequence sequence = context.sequence("seq");

        context.checking(new Expectations() {{
            oneOf(charSetup).reset(); inSequence(sequence);
            oneOf(charSetup).createClearChar(); will(returnValue(gfxChar)); inSequence(sequence);
            oneOf(buffer).setClearChar(gfxChar); inSequence(sequence);

            oneOf(charSetup).setAttribute(Attribute.BLINK); inSequence(sequence);
            oneOf(charSetup).setForeground(ColorName.find(34)); inSequence(sequence);
            oneOf(charSetup).setBackground(ColorName.find(41)); inSequence(sequence);
            oneOf(charSetup).createClearChar(); will(returnValue(gfxChar)); inSequence(sequence);
            oneOf(buffer).setClearChar(gfxChar); inSequence(sequence);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, "0", "5", "34", "41");
    }

    @Test
    public void specialHandlingOfCustomForegroundColor22() {
        testCustomForegroundColor(22);
    }

    @Test
    public void specialHandlingOfCustomForegroundColor67() {
        testCustomForegroundColor(67);
    }

    private void testCustomForegroundColor(final int colorCode) {
        context.checking(new Expectations() {{
            oneOf(charSetup).setForeground(colorCode);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, "38",
                "5", Integer.toString(colorCode));
    }

    @Test
    public void specialHandlingOfCustomBackgroundColor37() {
        testCustomBackgroundColor(37);
    }

    @Test
    public void specialHandlingOfCustomBackgroundColor255() {
        testCustomBackgroundColor(255);
    }

    private void testCustomBackgroundColor(final int colorCode) {
        context.checking(new Expectations() {{
            oneOf(charSetup).setBackground(colorCode);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, "48",
                "5", Integer.toString(colorCode));
    }


    @Test public void
    invalidCustomColorDoesNotThrowException() throws Exception {
        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, "48", "5");
    }

    @Test public void
    missingArgumentMeansReset() {
        final GfxChar gfxChar = context.mock(GfxChar.class);

        context.checking(new Expectations() {{
            oneOf(charSetup).reset();
            oneOf(charSetup).createClearChar(); will(returnValue(gfxChar));
            oneOf(buffer).setClearChar(gfxChar);

            oneOf(charSetup).setAttribute(Attribute.BRIGHT);
        }});

        DefaultSequenceRepository.executeAnsiSequence('m', sessionInfo, "", "1");
    }
}
