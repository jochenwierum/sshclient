package de.jowisoftware.sshclient.terminal.input.controlsequences;


import de.jowisoftware.sshclient.terminal.buffer.Position;
import org.jmock.Expectations;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ANSISequenceCursorTest extends AbstractSequenceTest {
    @Test
    public void testFEqualsH() {
        assertEquals(new DefaultSequenceRepository().getANSISequence('H')
                .getClass(),
                new DefaultSequenceRepository().getANSISequence('f').getClass());
    }

    @Test
    public void testHomePosition() {
        context.checking(new Expectations() {{
            oneOf(buffer).setCursorPosition(new Position(1, 1));
        }});

        DefaultSequenceRepository.executeAnsiSequence('f', sessionInfo);
    }

    @Test
    public void testCustomPositions() {
        context.checking(new Expectations() {{
            oneOf(buffer).setCursorPosition(new Position(5, 1));
            oneOf(buffer).setCursorPosition(new Position(7, 3));
        }});

        DefaultSequenceRepository.executeAnsiSequence('f', sessionInfo, "1", "5");
        DefaultSequenceRepository.executeAnsiSequence('f', sessionInfo, "3", "7");
    }

    @Test
    public void testPartialPositions() {
        context.checking(new Expectations() {{
            oneOf(buffer).setCursorPosition(new Position(1, 4));
            oneOf(buffer).setCursorPosition(new Position(3, 1));
            oneOf(buffer).setCursorPosition(new Position(1, 7));
            oneOf(buffer).setCursorPosition(new Position(12, 1));
        }});

        DefaultSequenceRepository.executeAnsiSequence('f', sessionInfo, "4", "");
        DefaultSequenceRepository.executeAnsiSequence('f', sessionInfo, "", "3");
        DefaultSequenceRepository.executeAnsiSequence('f', sessionInfo, "7", "0");
        DefaultSequenceRepository.executeAnsiSequence('f', sessionInfo, "0", "12");
    }

    @Test
    public void testMargin() {
        context.checking(new Expectations() {{
            oneOf(buffer).setMargin(1, 5);
            oneOf(buffer).setCursorPosition(new Position(1, 1));
            oneOf(buffer).setMargin(3, 7);
            oneOf(buffer).setCursorPosition(new Position(1, 1));
        }});

        DefaultSequenceRepository.executeAnsiSequence('r', sessionInfo, "1", "5");
        DefaultSequenceRepository.executeAnsiSequence('r', sessionInfo, "3", "7");
    }

    @Test
    public void testResetMargin() {
        context.checking(new Expectations() {{
            oneOf(buffer).resetMargin();
            oneOf(buffer).setCursorPosition(new Position(1, 1));
        }});

        DefaultSequenceRepository.executeAnsiSequence('r', sessionInfo);
    }

    @Test
    public void testMoveCursorLeft() {
        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(3, 4)));
            oneOf(buffer).setCursorPosition(new Position(2, 4));
        }});

        DefaultSequenceRepository.executeAnsiSequence('D', sessionInfo);

        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(6, 6)));
            oneOf(buffer).setCursorPosition(new Position(3, 6));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(7, 6)));
            oneOf(buffer).setCursorPosition(new Position(2, 6));
        }});

        DefaultSequenceRepository.executeAnsiSequence('D', sessionInfo, "3");
        DefaultSequenceRepository.executeAnsiSequence('D', sessionInfo, "5");
    }

    @Test
    public void testMoveCursorRightWithoutParams() {
        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(2, 4)));
            oneOf(buffer).setCursorPosition(new Position(3, 4));
        }});

        DefaultSequenceRepository.executeAnsiSequence('C', sessionInfo);
    }

    @Test
    public void testMoveCursorRightWithParams() {
        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(3, 6)));
            oneOf(buffer).setCursorPosition(new Position(6, 6));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(2, 6)));
            oneOf(buffer).setCursorPosition(new Position(7, 6));
        }});

        DefaultSequenceRepository.executeAnsiSequence('C', sessionInfo, "3");
        DefaultSequenceRepository.executeAnsiSequence('C', sessionInfo, "5");
    }

    @Test
    public void testMoveCursorAtBorder() {
        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(80, 1)));
            oneOf(buffer).setCursorPosition(new Position(80, 1));
        }});
        DefaultSequenceRepository.executeAnsiSequence('C', sessionInfo);

        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(1, 3)));
            oneOf(buffer).setCursorPosition(new Position(1, 3));
        }});
        DefaultSequenceRepository.executeAnsiSequence('D', sessionInfo);

        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(3, 1)));
            oneOf(buffer).setCursorPosition(new Position(3, 1));
        }});
        DefaultSequenceRepository.executeAnsiSequence('A', sessionInfo);

        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(3, 24)));
            oneOf(buffer).setCursorPosition(new Position(3, 24));
        }});
        DefaultSequenceRepository.executeAnsiSequence('B', sessionInfo);
    }

    @Test
    public void testMoveCursorRightWithParams0() {
        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(3, 6)));
            oneOf(buffer).setCursorPosition(new Position(4, 6));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(2, 6)));
            oneOf(buffer).setCursorPosition(new Position(3, 6));
        }});

        DefaultSequenceRepository.executeAnsiSequence('C', sessionInfo, "0");
        DefaultSequenceRepository.executeAnsiSequence('C', sessionInfo, "0");
    }

    @Test
    public void testMoveCursorUp() {
        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(3, 4)));
            oneOf(buffer).setCursorPosition(new Position(3, 3));
        }});

        DefaultSequenceRepository.executeAnsiSequence('A', sessionInfo);

        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(2, 6)));
            oneOf(buffer).setCursorPosition(new Position(2, 3));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(1, 7)));
            oneOf(buffer).setCursorPosition(new Position(1, 2));
        }});

        DefaultSequenceRepository.executeAnsiSequence('A', sessionInfo, "3");
        DefaultSequenceRepository.executeAnsiSequence('A', sessionInfo, "5");
    }

    @Test
    public void testMoveCursorDown() {
        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(3, 3)));
            oneOf(buffer).setCursorPosition(new Position(3, 4));
        }});

        DefaultSequenceRepository.executeAnsiSequence('B', sessionInfo);

        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(2, 3)));
            oneOf(buffer).setCursorPosition(new Position(2, 6));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(1, 2)));
            oneOf(buffer).setCursorPosition(new Position(1, 7));
        }});

        DefaultSequenceRepository.executeAnsiSequence('B', sessionInfo, "3");
        DefaultSequenceRepository.executeAnsiSequence('B', sessionInfo, "5");
    }

    @Test
    public void testInsertLine() {
        context.checking(new Expectations() {{
            oneOf(buffer).insertLines(1);
        }});
        DefaultSequenceRepository.executeAnsiSequence('L', sessionInfo);
    }

    @Test
    public void testInsert2Lines() {
        context.checking(new Expectations() {{
            oneOf(buffer).insertLines(2);
        }});

        DefaultSequenceRepository.executeAnsiSequence('L', sessionInfo, "2");
    }

    @Test
    public void testRemoveLine() {
        context.checking(new Expectations() {{
            oneOf(buffer).removeLines(1);
        }});
        DefaultSequenceRepository.executeAnsiSequence('M', sessionInfo);
    }

    @Test
    public void testRemove2Lines() {
        context.checking(new Expectations() {{
            oneOf(buffer).removeLines(2);
        }});

        DefaultSequenceRepository.executeAnsiSequence('M', sessionInfo, "2");
    }

    @Test
    public void testSetRow() {
        final Position pos = new Position(4, 5);
        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition(); will(returnValue(pos));
            oneOf(buffer).setCursorPosition(pos.withY(7));
        }});

        DefaultSequenceRepository.executeAnsiSequence('d', sessionInfo, "7");

        final Position pos2 = new Position(9, 8);
        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition(); will(returnValue(pos2));
            oneOf(buffer).setCursorPosition(pos2.withY(1));
        }});

        DefaultSequenceRepository.executeAnsiSequence('d', sessionInfo);

        final Position pos3 = new Position(10, 10);
        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition(); will(returnValue(pos3));
            oneOf(buffer).setCursorPosition(pos3.withY(1));
        }});

        DefaultSequenceRepository.executeAnsiSequence('d', sessionInfo, "0");
    }

    @Test
    public void testSetColumn() {
        final Position pos = new Position(4, 5);
        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition(); will(returnValue(pos));
            oneOf(buffer).setCursorPosition(pos.withX(4));
        }});

        DefaultSequenceRepository.executeAnsiSequence('G', sessionInfo, "4");

        final Position pos2 = new Position(9, 8);
        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition(); will(returnValue(pos2));
            oneOf(buffer).setCursorPosition(pos2.withX(1));
        }});

        DefaultSequenceRepository.executeAnsiSequence('G', sessionInfo);

        final Position pos3 = new Position(10, 10);
        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition(); will(returnValue(pos3));
            oneOf(buffer).setCursorPosition(pos3.withX(1));
        }});

        DefaultSequenceRepository.executeAnsiSequence('G', sessionInfo, "0");
    }
}
