package de.jowisoftware.sshclient.terminal;

import static org.junit.Assert.assertEquals;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.ui.GfxChar;

@RunWith(JMock.class)
public class ArrayBufferTest {
    private final Mockery context = new Mockery();
    private ArrayBuffer<GfxChar> buffer;
    private GfxChar nullChar;
    private GfxChar char1;
    private GfxChar char2;

    @Before
    public void setUp() {
        nullChar = context.mock(GfxChar.class, "nullChar");
        buffer = new ArrayBuffer<GfxChar>(nullChar, 80, 24);
        char1 = context.mock(GfxChar.class, "char1");
        char2 = context.mock(GfxChar.class, "char2");
    }

    private void assertChar(final int y, final int x, final GfxChar character) {
        assertEquals(character, buffer.getCharacter(y, x));
    }

    private void assertPosition(final int y, final int x) {
        assertEquals(x, buffer.getAbsoluteCursorPosition().x);
        assertEquals(y, buffer.getAbsoluteCursorPosition().y);
    }

    private void assertPositionInRoll(final int y, final int x) {
        assertEquals(x, buffer.getCursorPosition().x);
        assertEquals(y, buffer.getCursorPosition().y);
    }

    @Test
    public void testInitialValue() {
        assertPosition(1, 1);
        assertChar(1, 1, nullChar);
        assertChar(24, 80, nullChar);
    }

    @Test
    public void testCursorPosition() {
        buffer.setCursorPosition(new Position(2, 4));
        assertPosition(4, 2);

        buffer.setCursorPosition(new Position(1, 1));
        assertPosition(1, 1);
    }

    @Test
    public void testAddChar() {
        buffer.addCharacter(char1);
        assertPosition(1, 2);
        assertChar(1, 1, char1);
    }

    @Test
    public void testAddTwoChars() {
        buffer.addCharacter(char1);
        buffer.addCharacter(char2);
        assertPosition(1, 3);
        assertChar(1, 1, char1);
        assertChar(1, 2, char2);
    }

    @Test
    public void testAddNewLineChars() {
        buffer.addCharacter(char1);
        assertPosition(1, 2);

        buffer.addNewLine();
        assertPosition(2, 1);

        buffer.addCharacter(char2);
        assertChar(2, 1, char2);
        assertPosition(2, 2);
    }

    @Test
    public void testResize() {
        buffer.addCharacter(char1);
        buffer.setCursorPosition(new Position(24, 24));
        buffer.addCharacter(char2);
        buffer.newSize(24, 30);

        assertChar(1, 1, char1);
        assertChar(24, 24, char2);
        assertChar(30, 24, nullChar);

        buffer.setCursorPosition(new Position(23, 30));
        buffer.addCharacter(char1);
        assertChar(30, 23, char1);
    }

    @Test
    public void testTooLongLine() {
        buffer.setCursorPosition(new Position(80, 1));
        buffer.addCharacter(char1);
        assertPosition(1, 80);
        assertChar(1, 80, char1);
        buffer.addCharacter(char2);
        assertPosition(1, 80);
        assertChar(1, 80, char2);
    }

    @Test
    public void setRollRangedCursorSet() {
        buffer.setRollRange(3, 10);
        buffer.setCursorPosition(new Position(1, 2));
        buffer.addCharacter(char1);

        assertChar(2, 1, nullChar);
        assertChar(4, 1, char1);
        assertPositionInRoll(2, 2);
        assertPosition(4, 2);
    }

    @Test
    public void testFullBuffer() {
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char2);

        buffer.setCursorPosition(new Position(1, 24));
        buffer.addCharacter(char2); buffer.addNewLine();
        buffer.addCharacter(char1);

        assertChar(1, 1, char2);
        assertChar(23, 1, char2);
        assertChar(24, 1, char1);
    }


    @Test
    public void testSaveSet() {
        buffer.setSafeCursorPosition(new Position(-7, -3));
        assertPosition(1, 1);
        buffer.setSafeCursorPosition(new Position(99, 99));
        assertPosition(24, 80);
        buffer.setSafeCursorPosition(new Position(5, 200));
        assertPosition(24, 80);
        buffer.setSafeCursorPosition(new Position(4, 8));
        assertPosition(8, 4);
    }

    @Test
    public void testMoveCursorUpAndRoll() {
        buffer.setRollRange(2, 4);
        buffer.addCharacter(char2); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char2); buffer.addNewLine();
        buffer.addCharacter(char1);

        buffer.moveCursorUpAndRoll();
        assertPosition(3, 1);
        assertChar(1, 1, char2);
        assertChar(2, 1, char1);
        assertChar(4, 1, char1);

        buffer.moveCursorUpAndRoll();
        assertPosition(2, 1);
        assertChar(1, 1, char2);
        assertChar(2, 1, char1);
        assertChar(4, 1, char1);

        buffer.moveCursorUpAndRoll();
        assertPosition(2, 1);
        assertChar(1, 1, char2);
        assertChar(2, 1, nullChar);
        assertChar(3, 1, char1);
        assertChar(4, 1, char2);
    }

    @Test
    public void testMoveCursorUpAndRollWithoutRoll() {
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char2); buffer.addNewLine();
        buffer.addCharacter(char1);

        buffer.moveCursorUpAndRoll();
        buffer.moveCursorUpAndRoll();
        buffer.moveCursorUpAndRoll();
        assertPosition(1, 1);
        assertChar(3, 1, char1);
        assertChar(2, 1, char2);
        assertChar(1, 1, char1);
    }

    @Test
    public void testMoveCursorDownAndRoll() {
        buffer.setRollRange(2, 3);
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char2); buffer.addNewLine();
        buffer.addCharacter(char1);

        buffer.setCursorPosition(buffer.getCursorPosition().offset(0, -1));
        buffer.moveCursorDownAndRoll(false);
        assertPosition(3, 2);
        assertChar(1, 1, char1);
        assertChar(2, 1, char2);

        buffer.moveCursorDownAndRoll(false);
        assertPosition(3, 2);
        assertChar(1, 1, char1);
        assertChar(2, 1, char1);
    }

    @Test
    public void testMoveCursorDownAndRollWithoutRoll() {
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.setCursorPosition(new Position(1, 23));
        buffer.addCharacter(char2); buffer.addNewLine();
        buffer.addCharacter(char1);

        buffer.moveCursorDownAndRoll(false);
        assertPosition(24, 2);
        assertChar(1, 1, nullChar);
        assertChar(22, 1, char2);
        assertChar(23, 1, char1);
        assertChar(24, 1, nullChar);
    }

    @Test
    public void testMoveCursorDownAndRollColReset() {
        buffer.setRollRange(2, 3);
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char2); buffer.addNewLine();
        buffer.addCharacter(char1);

        buffer.setCursorPosition(buffer.getCursorPosition().offset(0, -1));
        buffer.moveCursorDownAndRoll(true);
        assertPosition(3, 1);
        assertChar(1, 1, char1);
        assertChar(2, 1, char2);

        buffer.moveCursorDownAndRoll(true);
        assertPosition(3, 1);
        assertChar(1, 1, char1);
        assertChar(2, 1, char1);
    }

    @Test
    public void testMoveCursorDownAndRollWithoutRollAndColReset() {
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.setCursorPosition(new Position(1, 23));
        buffer.addCharacter(char2); buffer.addNewLine();
        buffer.addCharacter(char1);

        buffer.moveCursorDownAndRoll(true);
        assertPosition(24, 1);
        assertChar(1, 1, nullChar);
        assertChar(22, 1, char2);
        assertChar(23, 1, char1);
        assertChar(24, 1, nullChar);
    }

    @Test
    public void testEraseOneLine() {
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addCharacter(char1);
            buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addCharacter(char1);
            buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1);

        buffer.erase(new Range(new Position(1, 2),
                new Position(80, 2)));
        buffer.erase(new Range(new Position(1, 4),
                new Position(80, 4)));

        assertChar(1, 1, char1);
        assertChar(3, 1, char1);
        assertChar(5, 1, char1);
        assertChar(2, 1, nullChar);
        assertChar(2, 3, nullChar);
        assertChar(4, 1, nullChar);
        assertChar(4, 3, nullChar);
    }

    @Test
    public void testEraseAll() {
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1);
        buffer.setCursorPosition(new Position(80, 23));
        buffer.addCharacter(char1);

        buffer.erase(new Range(new Position(1, 1),
                new Position(80, 23)));

        assertChar(1, 1, nullChar);
        assertChar(23, 80, nullChar);
        assertChar(2, 1, nullChar);
        assertChar(5, 5, nullChar);
    }

    @Test
    public void testReaseRange() {
        buffer.setCursorPosition(new Position(2, 3));
        buffer.addCharacter(char1); buffer.addCharacter(char2);
        buffer.setCursorPosition(new Position(6, 5));
        buffer.addCharacter(char2);
        buffer.setCursorPosition(new Position(6, 6));
        buffer.addCharacter(char2); buffer.addCharacter(char1);

        buffer.erase(new Range(new Position(3, 3),
                new Position(6, 6)));

        assertChar(3, 2, char1);
        assertChar(3, 3, nullChar);
        assertChar(5, 6, nullChar);
        assertChar(6, 6, nullChar);
        assertChar(6, 7, char1);
    }

    @Test
    public void testReaseInLineRange() {
        buffer.addCharacter(char1);
        buffer.addCharacter(char2); buffer.addCharacter(char2);
            buffer.addCharacter(char2);
        buffer.addCharacter(char1);

        buffer.erase(new Range(new Position(2, 1),
                new Position(4, 1)));

        assertChar(1, 1, char1);
        assertChar(1, 2, nullChar);
        assertChar(1, 3, nullChar);
        assertChar(1, 4, nullChar);
        assertChar(1, 5, char1);
    }
}
