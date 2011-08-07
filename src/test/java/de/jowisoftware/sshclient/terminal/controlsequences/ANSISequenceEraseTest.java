package de.jowisoftware.sshclient.terminal.controlsequences;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.buffer.Range;

@RunWith(JMock.class)
public class ANSISequenceEraseTest extends AbstractSequenceTest {

    private void testEraseCursorToButtom(final Position cursorPosition,
            final Position screenSize) {
        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
                will(returnValue(cursorPosition));
            oneOf(buffer).getSize();
                will(returnValue(screenSize));
            oneOf(buffer).erase(new Range(cursorPosition, screenSize));
        }});

        DefaultSequenceRepository.executeAnsiSequence('J', sessionInfo);
    }

    @Test
    public void testEraseCursorToBottom1() {
        testEraseCursorToButtom(new Position(5, 7), new Position(24, 80));
    }

    @Test
    public void testEraseCursorToBottom2() {
        testEraseCursorToButtom(new Position(4, 9), new Position(12, 60));
    }

    private void testEraseFromTop(final Position cursorPosition) {
        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
                will(returnValue(cursorPosition));
            oneOf(buffer).erase(cursorPosition.toRange());
        }});

        DefaultSequenceRepository.executeAnsiSequence('J', sessionInfo, "1");
    }

    @Test
    public void testEraseFromTop1() {
        testEraseFromTop(new Position(2, 5));
    }

    @Test
    public void testEraseFromTop2() {
        testEraseFromTop(new Position(4, 8));
    }

    private void testErase(final Position position) {
        context.checking(new Expectations() {{
            allowing(buffer).getSize();
            will(returnValue(position));
            oneOf(buffer).erase(position.toRange());
        }});

        DefaultSequenceRepository.executeAnsiSequence('J', sessionInfo, "2");
    }

    @Test
    public void testErase1() {
        final Position position = new Position(80, 24);
        testErase(position);
    }

    @Test
    public void testErase2() {
        testErase(new Position(60, 60));
    }

    private void testEraseRestOfLine(final Position cursorPos,
            final Position screenSize) {
        context.checking(new Expectations() {{
            allowing(buffer).getCursorPosition();
                will(returnValue(cursorPos));
            allowing(buffer).getSize();
                will(returnValue(screenSize));

            oneOf(buffer).erase(new Range(
                    cursorPos,
                    new Position(screenSize.x, cursorPos.y)));
        }});

        DefaultSequenceRepository.executeAnsiSequence('K', sessionInfo);
    }

    @Test
    public void testEraseRestOfLine1() {
        testEraseRestOfLine(new Position(2, 5), new Position(80, 24));
    }

    @Test
    public void testEraseRestOfLine2() {
        testEraseRestOfLine(new Position(4, 7), new Position(60, 60));
    }

    private void testStartOfLine(final Position position) {
        context.checking(new Expectations() {{
            allowing(buffer).getCursorPosition();
            will(returnValue(position));
            oneOf(buffer).erase(new Range(
                    new Position(1, position.y),
                    position));
        }});

        DefaultSequenceRepository.executeAnsiSequence('K', sessionInfo, "1");
    }

    @Test
    public void testEraseStartOfLine1() {
        testStartOfLine(new Position(2, 5));
    }

    @Test
    public void testEraseStartOfLine2() {
        testStartOfLine(new Position(4, 7));
    }

    private void testEraseLine(final Position cursorPos,
            final Position screenSize) {
        context.checking(new Expectations() {{
            allowing(buffer).getCursorPosition();
                will(returnValue(cursorPos));
            allowing(buffer).getSize();
                will(returnValue(screenSize));
            oneOf(buffer).erase(new Range(
                    new Position(1, cursorPos.y),
                    new Position(screenSize.x, cursorPos.y)));
        }});

        DefaultSequenceRepository.executeAnsiSequence('K', sessionInfo, "2");
    }

    @Test
    public void testEraseLine1() {
        testEraseLine(new Position(2, 5), new Position(80, 24));
    }

    @Test
    public void testEraseLine2() {
        testEraseLine(new Position(7, 8), new Position(60, 30));
    }

    private void testEraseXChars(final Position position, final int charCount,
            final String ... args) {
        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
            will(returnValue(position));
            oneOf(buffer).erase(new Range(
                    position,
                    position.offset(charCount, 0)));
        }});

        DefaultSequenceRepository.executeAnsiSequence('X', sessionInfo, args);
    }

    @Test
    public void testEraseChars() {
        testEraseXChars(new Position(2, 2), 1);
        testEraseXChars(new Position(3, 6), 1, "0");
        testEraseXChars(new Position(1, 7), 3, "3");
        testEraseXChars(new Position(2, 5), 6, "6");
    }
}
