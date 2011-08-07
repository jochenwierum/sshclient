package de.jowisoftware.sshclient.terminal.controlsequences;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.DisplayType;
import de.jowisoftware.sshclient.terminal.buffer.BufferSelection;
import de.jowisoftware.sshclient.terminal.buffer.Position;

@RunWith(JMock.class)
public class ANSISequenceHighLowTest extends AbstractSequenceTest {
    @Test
    public void testHandleNumblock() {
        context.checking(new Expectations() {{
            oneOf(keyboardFeedback).setCursorKeysIsAppMode(false);
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?1");
    }

    @Test
    public void testHandleNumblockOff() {
        context.checking(new Expectations() {{
            oneOf(keyboardFeedback).setCursorKeysIsAppMode(true);
        }});

        DefaultSequenceRepository.executeAnsiSequence('h', sessionInfo, "?1");
    }

    @Test
    public void testTerminalWidth132() {
        context.checking(new Expectations() {{
            final Position size = new Position(132, 24);
            oneOf(visualFeedback).setDisplayType(DisplayType.FIXED132X24);
            oneOf(buffer).getSize(); will(returnValue(size));
            oneOf(buffer).erase(size.toRange());
            oneOf(buffer).setCursorPosition(new Position(1, 1));
        }});

        DefaultSequenceRepository.executeAnsiSequence('h', sessionInfo, "?3");
    }

    @Test
    public void testTerminalWidth80() {
        context.checking(new Expectations() {{
            final Position size = new Position(80, 24);
            oneOf(visualFeedback).setDisplayType(DisplayType.FIXED80X24);
            oneOf(buffer).getSize(); will(returnValue(size));
            oneOf(buffer).erase(size.toRange());
            oneOf(buffer).setCursorPosition(new Position(1, 1));
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?3");
    }

    @Test
    public void testOriginModeHigh() {
        context.checking(new Expectations() {{
            oneOf(buffer).setCursorRelativeToMargin(true);
            oneOf(buffer).setCursorPosition(new Position(1, 1));
        }});

        DefaultSequenceRepository.executeAnsiSequence('h', sessionInfo, "?6");
    }

    @Test
    public void testOriginModeLow() {
        context.checking(new Expectations() {{
            oneOf(buffer).setCursorRelativeToMargin(false);
            oneOf(buffer).setCursorPosition(new Position(1, 1));
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?6");
    }

    @Test
    public void testHandleAlternateScreenBuffer() {
        context.checking(new Expectations() {{
            oneOf(buffer).switchBuffer(BufferSelection.ALTERNATIVE);
        }});

        DefaultSequenceRepository.executeAnsiSequence('h', sessionInfo, "?1047");
    }

    @Test
    public void testHandleNormalScreenBufferFromAlternativeScreenBuffer() {
        final Position size = new Position(80, 24);
        context.checking(new Expectations() {{
            oneOf(buffer).getSelectedBuffer(); will(returnValue(BufferSelection.ALTERNATIVE));
            oneOf(buffer).getSize(); will(returnValue(size));
            oneOf(buffer).erase(size.toRange());
            oneOf(buffer).switchBuffer(BufferSelection.PRIMARY);
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?1047");
    }

    @Test
    public void testHandleNormalScreenBufferFromNormalScreenBuffer() {
        context.checking(new Expectations() {{
            oneOf(buffer).getSelectedBuffer(); will(returnValue(BufferSelection.PRIMARY));
            oneOf(buffer).switchBuffer(BufferSelection.PRIMARY);
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?1047");
    }

    @Test
    public void testSaveCursorPosition() {
        context.checking(new Expectations() {{
            oneOf(buffer).saveCursorPosition();
        }});

        DefaultSequenceRepository.executeAnsiSequence('h', sessionInfo, "?1048");
    }

    @Test
    public void testRestoreCursorPosition() {
        context.checking(new Expectations() {{
            oneOf(buffer).restoreCursorPosition();
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?1048");
    }

    @Test
    public void testHandleAlternateScreenBufferWithRestoredCursor() {
        context.checking(new Expectations() {{
            oneOf(buffer).switchBuffer(BufferSelection.PRIMARY);
            oneOf(buffer).restoreCursorPosition();
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?1049");
    }

    @Test
    public void testHandleAlternateScreenBufferWithSavedCursor() {
        final Position size = new Position(80, 24);
        context.checking(new Expectations() {{
            oneOf(buffer).saveCursorPosition();
            oneOf(buffer).switchBuffer(BufferSelection.ALTERNATIVE);
            oneOf(buffer).getSize(); will(returnValue(size));
            oneOf(buffer).erase(size.toRange());
        }});

        DefaultSequenceRepository.executeAnsiSequence('h', sessionInfo, "?1049");
    }

    @Test
    public void testHandleWrapAroundOn() {
        context.checking(new Expectations() {{
            oneOf(buffer).setAutoWrap(true);
        }});

        DefaultSequenceRepository.executeAnsiSequence('h', sessionInfo, "?7");
    }

    @Test
    public void testHandleWrapAroundOff() {
        context.checking(new Expectations() {{
            oneOf(buffer).setAutoWrap(false);
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?7");
    }

    @Test
    public void testHandleShowCursorOn() {
        context.checking(new Expectations() {{
            oneOf(buffer).setShowCursor(true);
        }});

        DefaultSequenceRepository.executeAnsiSequence('h', sessionInfo, "?25");
    }

    @Test
    public void testHandleShowCursorOff() {
        context.checking(new Expectations() {{
            oneOf(buffer).setShowCursor(false);
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?25");
    }
}
