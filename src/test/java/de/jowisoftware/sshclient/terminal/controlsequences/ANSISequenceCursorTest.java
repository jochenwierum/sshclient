package de.jowisoftware.sshclient.terminal.controlsequences;


import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.Position;
import de.jowisoftware.sshclient.util.SequenceUtils;

@RunWith(JMock.class)
public class ANSISequenceCursorTest extends AbstractSequenceTest {
    @Test
    public void testHomePosition() {
        context.checking(new Expectations() {{
            oneOf(buffer).setAbsoluteCursorPosition(new Position(1, 1));
        }});

        SequenceUtils.getANSISequence('H').process(sessionInfo);
    }

    @Test
    public void testCustomPositionsWithH() {
        context.checking(new Expectations() {{
            oneOf(buffer).setCursorPosition(new Position(5, 1));
            oneOf(buffer).setCursorPosition(new Position(7, 3));
        }});

        SequenceUtils.getANSISequence('H').process(sessionInfo, "1", "5");
        SequenceUtils.getANSISequence('H').process(sessionInfo, "3", "7");
    }

    @Test
    public void testCustomPositionsWithf() {
        context.checking(new Expectations() {{
            oneOf(buffer).setCursorPosition(new Position(5, 1));
            oneOf(buffer).setCursorPosition(new Position(7, 3));
        }});

        SequenceUtils.getANSISequence('f').process(sessionInfo, "1", "5");
        SequenceUtils.getANSISequence('f').process(sessionInfo, "3", "7");
    }

    @Test
    public void testSetupRoll() {
        context.checking(new Expectations() {{
            oneOf(buffer).setRollRange(1, 5);
            oneOf(buffer).setCursorPosition(new Position(1, 1));
            oneOf(buffer).setRollRange(3, 7);
            oneOf(buffer).setCursorPosition(new Position(1, 1));
        }});

        SequenceUtils.getANSISequence('r').process(sessionInfo, "1", "5");
        SequenceUtils.getANSISequence('r').process(sessionInfo, "3", "7");
    }

    @Test
    public void testRemoveRoll() {
        context.checking(new Expectations() {{
            oneOf(buffer).deleteRollRange();
            oneOf(buffer).setCursorPosition(new Position(1, 1));
        }});

        SequenceUtils.getANSISequence('r').process(sessionInfo);
    }

    @Test
    public void testMoveCursorLeft() {
        context.checking(new Expectations() {{
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
    public void testMoveCursorRight() {
        context.checking(new Expectations() {{
            oneOf(buffer).getCursorPosition();
                will(returnValue(new Position(2, 4)));
            oneOf(buffer).setCursorPosition(new Position(3, 4));
        }});

        SequenceUtils.getANSISequence('C').process(sessionInfo);

        context.checking(new Expectations() {{
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
    public void testMoveCursorUp() {
        context.checking(new Expectations() {{
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
}
