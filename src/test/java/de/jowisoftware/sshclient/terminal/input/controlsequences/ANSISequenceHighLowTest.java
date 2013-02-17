package de.jowisoftware.sshclient.terminal.input.controlsequences;

import org.jmock.Expectations;
import org.junit.Test;

import de.jowisoftware.sshclient.terminal.buffer.BufferSelection;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.events.DisplayType;

public class ANSISequenceHighLowTest extends AbstractSequenceTest {
    @Test
    public void handleNumblock() {
        context.checking(new Expectations() {{
            oneOf(keyboardFeedback).newCursorKeysIsAppMode(false);
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?1");
    }

    @Test
    public void handleNumblockOff() {
        context.checking(new Expectations() {{
            oneOf(keyboardFeedback).newCursorKeysIsAppMode(true);
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
    public void handleAlternateScreenBuffer() {
        context.checking(new Expectations() {{
            oneOf(buffer).switchBuffer(BufferSelection.ALTERNATE);
        }});

        DefaultSequenceRepository.executeAnsiSequence('h', sessionInfo, "?1047");
    }

    @Test
    public void handleNormalScreenBufferFromAlternativeScreenBuffer() {
        final Position size = new Position(80, 24);
        context.checking(new Expectations() {{
            oneOf(buffer).getSelectedBuffer(); will(returnValue(BufferSelection.ALTERNATE));
            oneOf(buffer).getSize(); will(returnValue(size));
            oneOf(buffer).erase(size.toRange());
            oneOf(buffer).switchBuffer(BufferSelection.PRIMARY);
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?1047");
    }

    @Test
    public void handleNormalScreenBufferFromNormalScreenBuffer() {
        context.checking(new Expectations() {{
            oneOf(buffer).getSelectedBuffer(); will(returnValue(BufferSelection.PRIMARY));
            oneOf(buffer).switchBuffer(BufferSelection.PRIMARY);
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?1047");
    }

    @Test
    public void testSaveCursorPosition() {
        context.checking(new Expectations() {{
            oneOf(sessionInfo).saveState();
        }});

        DefaultSequenceRepository.executeAnsiSequence('h', sessionInfo, "?1048");
    }

    @Test
    public void testRestoreCursorPosition() {
        context.checking(new Expectations() {{
            oneOf(sessionInfo).restoreState();
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?1048");
    }

    @Test
    public void handleAlternateScreenBufferWithRestoredCursor() {
        context.checking(new Expectations() {{
            oneOf(buffer).switchBuffer(BufferSelection.PRIMARY);
            oneOf(sessionInfo).restoreState();
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?1049");
    }

    @Test
    public void handleAlternateScreenBufferWithSavedCursor() {
        final Position size = new Position(80, 24);
        context.checking(new Expectations() {{
            oneOf(sessionInfo).saveState();
            oneOf(buffer).switchBuffer(BufferSelection.ALTERNATE);
            oneOf(buffer).getSize(); will(returnValue(size));
            oneOf(buffer).erase(size.toRange());
        }});

        DefaultSequenceRepository.executeAnsiSequence('h', sessionInfo, "?1049");
    }

    @Test
    public void handleWrapAroundOn() {
        context.checking(new Expectations() {{
            oneOf(buffer).setAutoWrap(true);
        }});

        DefaultSequenceRepository.executeAnsiSequence('h', sessionInfo, "?7");
    }

    @Test
    public void handleWrapAroundOff() {
        context.checking(new Expectations() {{
            oneOf(buffer).setAutoWrap(false);
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?7");
    }

    @Test
    public void handleShowCursorOn() {
        context.checking(new Expectations() {{
            oneOf(buffer).setShowCursor(true);
        }});

        DefaultSequenceRepository.executeAnsiSequence('h', sessionInfo, "?25");
    }

    @Test
    public void handleShowCursorOff() {
        context.checking(new Expectations() {{
            oneOf(buffer).setShowCursor(false);
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?25");
    }

    @Test
    public void handleMultipleArguments() {
        context.checking(new Expectations() {{
            oneOf(buffer).setAutoWrap(false);
            oneOf(buffer).setShowCursor(false);
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?25", "7");
    }

    @Test
    public void noExceptionWhenNoArgumentsAreGiven() {
        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo);
    }

    @Test
    public void handleInverseModeOff() {
        context.checking(new Expectations() {{
            oneOf(visualFeedback).newInverseMode(false);
        }});

        DefaultSequenceRepository.executeAnsiSequence('l', sessionInfo, "?5");
    }

    @Test
    public void handleInverseModeOn() {
        context.checking(new Expectations() {{
            oneOf(visualFeedback).newInverseMode(true);
        }});

        DefaultSequenceRepository.executeAnsiSequence('h', sessionInfo, "?5");
    }
}
