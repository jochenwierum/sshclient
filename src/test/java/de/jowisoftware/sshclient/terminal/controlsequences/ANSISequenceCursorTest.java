package de.jowisoftware.sshclient.terminal.controlsequences;


import static org.junit.Assert.assertSame;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.Position;
import de.jowisoftware.sshclient.util.SequenceUtils;

@RunWith(JMock.class)
public class ANSISequenceCursorTest extends AbstractSequenceTest {
    @Test
    public void testFEqualsH() {
        assertSame(SequenceUtils.getANSISequence('H'),
                SequenceUtils.getANSISequence('f'));
    }

    @Test
    public void testHomePosition() {
        context.checking(new Expectations() {{
            oneOf(buffer).setCursorPosition(new Position(1, 1));
        }});

        SequenceUtils.getANSISequence('f').process(sessionInfo);
    }

    @Test
    public void testCustomPositions() {
        context.checking(new Expectations() {{
            oneOf(buffer).setCursorPosition(new Position(5, 1));
            oneOf(buffer).setCursorPosition(new Position(7, 3));
        }});

        SequenceUtils.getANSISequence('f').process(sessionInfo, "1", "5");
        SequenceUtils.getANSISequence('f').process(sessionInfo, "3", "7");
    }

    @Test
    public void testPartialPositions() {
        context.checking(new Expectations() {{
            oneOf(buffer).setCursorPosition(new Position(1, 4));
            oneOf(buffer).setCursorPosition(new Position(3, 1));
            oneOf(buffer).setCursorPosition(new Position(1, 7));
            oneOf(buffer).setCursorPosition(new Position(12, 1));
        }});

        SequenceUtils.getANSISequence('f').process(sessionInfo, "4", "");
        SequenceUtils.getANSISequence('f').process(sessionInfo, "", "3");
        SequenceUtils.getANSISequence('f').process(sessionInfo, "7", "0");
        SequenceUtils.getANSISequence('f').process(sessionInfo, "0", "12");
    }

    @Test
    public void testMargin() {
        context.checking(new Expectations() {{
            oneOf(buffer).setMargin(1, 5);
            oneOf(buffer).setCursorPosition(new Position(1, 1));
            oneOf(buffer).setMargin(3, 7);
            oneOf(buffer).setCursorPosition(new Position(1, 1));
        }});

        SequenceUtils.getANSISequence('r').process(sessionInfo, "1", "5");
        SequenceUtils.getANSISequence('r').process(sessionInfo, "3", "7");
    }

    @Test
    public void testResetMargin() {
        context.checking(new Expectations() {{
            oneOf(buffer).resetMargin();
            oneOf(buffer).setCursorPosition(new Position(1, 1));
        }});

        SequenceUtils.getANSISequence('r').process(sessionInfo);
    }

    @Test
    public void testMoveCursorLeft() {
        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(3, 4)));
            oneOf(buffer).setCursorPosition(new Position(2, 4));
        }});

        SequenceUtils.getANSISequence('D').process(sessionInfo);

        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(6, 6)));
            oneOf(buffer).setCursorPosition(new Position(3, 6));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(7, 6)));
            oneOf(buffer).setCursorPosition(new Position(2, 6));
        }});

        SequenceUtils.getANSISequence('D').process(sessionInfo, "3");
        SequenceUtils.getANSISequence('D').process(sessionInfo, "5");
    }

    @Test
    public void testMoveCursorRightWithoutParams() {
        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(2, 4)));
            oneOf(buffer).setCursorPosition(new Position(3, 4));
        }});

        SequenceUtils.getANSISequence('C').process(sessionInfo);
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

        SequenceUtils.getANSISequence('C').process(sessionInfo, "3");
        SequenceUtils.getANSISequence('C').process(sessionInfo, "5");
    }

    @Test
    public void testMoveCursorAtBorder() {
        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(80, 1)));
            oneOf(buffer).setCursorPosition(new Position(80, 1));
        }});
        SequenceUtils.getANSISequence('C').process(sessionInfo);

        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(1, 3)));
            oneOf(buffer).setCursorPosition(new Position(1, 3));
        }});
        SequenceUtils.getANSISequence('D').process(sessionInfo);

        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(3, 1)));
            oneOf(buffer).setCursorPosition(new Position(3, 1));
        }});
        SequenceUtils.getANSISequence('A').process(sessionInfo);

        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(3, 24)));
            oneOf(buffer).setCursorPosition(new Position(3, 24));
        }});
        SequenceUtils.getANSISequence('B').process(sessionInfo);
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

        SequenceUtils.getANSISequence('C').process(sessionInfo, "0");
        SequenceUtils.getANSISequence('C').process(sessionInfo, "0");
    }

    @Test
    public void testMoveCursorUp() {
        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(3, 4)));
            oneOf(buffer).setCursorPosition(new Position(3, 3));
        }});

        SequenceUtils.getANSISequence('A').process(sessionInfo);

        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(2, 6)));
            oneOf(buffer).setCursorPosition(new Position(2, 3));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(1, 7)));
            oneOf(buffer).setCursorPosition(new Position(1, 2));
        }});

        SequenceUtils.getANSISequence('A').process(sessionInfo, "3");
        SequenceUtils.getANSISequence('A').process(sessionInfo, "5");
    }

    @Test
    public void testMoveCursorDown() {
        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(3, 3)));
            oneOf(buffer).setCursorPosition(new Position(3, 4));
        }});

        SequenceUtils.getANSISequence('B').process(sessionInfo);

        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(2, 3)));
            oneOf(buffer).setCursorPosition(new Position(2, 6));
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(1, 2)));
            oneOf(buffer).setCursorPosition(new Position(1, 7));
        }});

        SequenceUtils.getANSISequence('B').process(sessionInfo, "3");
        SequenceUtils.getANSISequence('B').process(sessionInfo, "5");
    }

    @Test
    public void testInsertLine() {
        context.checking(new Expectations() {{
            oneOf(buffer).insertLines(1);
        }});
        SequenceUtils.getANSISequence('L').process(sessionInfo);
    }

    @Test
    public void testInsert2Lines() {
        context.checking(new Expectations() {{
            oneOf(buffer).insertLines(2);
        }});

        SequenceUtils.getANSISequence('L').process(sessionInfo, "2");
    }
}
