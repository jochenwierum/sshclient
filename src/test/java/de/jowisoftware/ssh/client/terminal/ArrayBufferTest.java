package de.jowisoftware.ssh.client.terminal;

import static org.junit.Assert.assertEquals;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.ssh.client.ui.GfxChar;

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
        buffer = new ArrayBuffer<GfxChar>(null, nullChar, 80, 24);
        char1 = context.mock(GfxChar.class, "char1");
        char2 = context.mock(GfxChar.class, "char2");
    }

    private void assertChar(final int x, final int y, final GfxChar character) {
        assertEquals(character, buffer.getCharacter(y, x));
    }

    private void assertPosition(final int x, final int y) {
        assertEquals(x, buffer.getCursorPosition().getX());
        assertEquals(y, buffer.getCursorPosition().getY());
    }

    @Test
    public void testInitialValue() {
        assertPosition(1, 1);
        assertChar(1, 1, nullChar);
        assertChar(80, 24, nullChar);
    }

    @Test
    public void testCursorPosition() {
        buffer.setCursorPosition(new CursorPosition(2, 4));
        assertPosition(2, 4);

        buffer.setCursorPosition(new CursorPosition(1, 1));
        assertPosition(1, 1);
    }

    @Test
    public void testAddChar() {
        buffer.addCharacter(char1);
        assertPosition(2, 1);
        assertChar(1, 1, char1);
    }

    @Test
    public void testAddTwoChars() {
        buffer.addCharacter(char1);
        buffer.addCharacter(char2);
        assertPosition(3, 1);
        assertChar(1, 1, char1);
        assertChar(2, 1, char2);
    }

    @Test
    public void testAddNewLineChars() {
        buffer.addCharacter(char1);
        assertPosition(2, 1);

        buffer.addNewLine();
        assertPosition(1, 2);

        buffer.addCharacter(char2);
        assertChar(1, 2, char2);
        assertPosition(2, 2);
    }

    @Test
    public void testEraseDown() {
        /*
         * delete from y to bottom, including y
         *   1234
         * 1 x
         * 2 x
         * 3 xy
         * 4 x
         */

        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char2); buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char2); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(2, 3));
        buffer.eraseToBottom();

        assertChar(1, 1, char1);
        assertChar(1, 2, char1);
        assertChar(1, 3, char2);
        assertChar(2, 3, nullChar);
        assertChar(1, 4, nullChar);
    }

    @Test
    public void testEraseRestOfLine() {
        /*
         * delete all y
         *   12345
         * 1 x
         * 2 xyyy
         * 3 x
         */

        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addCharacter(char2);
            buffer.addCharacter(char1); buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char2); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(2, 2));
        buffer.eraseRestOfLine();

        assertChar(1, 1, char1);
        assertChar(1, 2, char1);
        assertChar(2, 2, nullChar);
        assertChar(2, 3, nullChar);
        assertChar(2, 4, nullChar);
        assertChar(1, 3, char2);
    }

    @Test
    public void testEraseStartOfLine() {
        /*
         * delete all y
         *   1234
         * 1 x
         * 2 yyxx
         * 3 x
         */

        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addCharacter(char1);
            buffer.addCharacter(char1); buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(2, 2));
        buffer.eraseStartOfLine();

        assertChar(1, 1, char1);
        assertChar(1, 2, nullChar);
        assertChar(2, 2, nullChar);
        assertChar(3, 2, char1);
        assertChar(4, 2, char1);
        assertChar(1, 3, char1);
    }

    @Test
    public void testErase() {
        /*
         * delete all x
         *   1234
         * 1 x
         * 2 xx
         * 3 x
         */

        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(2, 2));
        buffer.erase();

        assertChar(1, 1, nullChar);
        assertChar(1, 2, nullChar);
        assertChar(2, 2, nullChar);
        assertChar(1, 3, nullChar);
    }

    @Test
    public void testEraseLine() {
        /*
         * delete all y
         *   1234
         * 1 x
         * 2 yyy
         * 3 x
         */

        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addCharacter(char1);
            buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(2, 2));
        buffer.eraseLine();

        assertChar(1, 1, char1);
        assertChar(1, 2, nullChar);
        assertChar(2, 2, nullChar);
        assertChar(3, 2, nullChar);
        assertChar(1, 3, char1);
    }

    @Test
    public void testEraseFromTop() {
        /*
         * delete all y
         *   1234
         * 1 y
         * 2 yyx
         * 3 x
         */

        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addCharacter(char1);
            buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(2, 2));
        buffer.eraseFromTop();

        assertChar(1, 1, nullChar);
        assertChar(1, 2, nullChar);
        assertChar(2, 2, nullChar);
        assertChar(3, 2, char1);
        assertChar(1, 3, char1);
    }

    @Test
    public void testResize() {
        buffer.addCharacter(char1);
        buffer.setCursorPosition(new CursorPosition(24, 24));
        buffer.addCharacter(char2);
        buffer.newSize(24, 30);

        assertChar(1, 1, char1);
        assertChar(24, 24, char2);
        assertChar(24, 30, nullChar);

        buffer.setCursorPosition(new CursorPosition(23, 30));
        buffer.addCharacter(char1);
        assertChar(23, 30, char1);
    }

    @Test
    public void testTooLongLine() {
        buffer.setCursorPosition(new CursorPosition(80, 1));
        buffer.addCharacter(char1);
        assertPosition(1, 2);
        buffer.addCharacter(char2);
        assertPosition(2, 2);
    }

    @Test
    public void setRollRangedCursorSet() {
        buffer.setRollRange(3, 10);
        buffer.setCursorPosition(new CursorPosition(1, 2));
        buffer.addCharacter(char1);

        assertChar(1, 2, nullChar);
        assertChar(1, 4, char1);
        assertPosition(2, 2);
    }

    @Test
    public void testFullBuffer() {
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char2);

        buffer.setCursorPosition(new CursorPosition(1, 24));
        buffer.addCharacter(char2); buffer.addNewLine();
        buffer.addCharacter(char1);

        assertChar(1, 1, char2);
        assertChar(1, 23, char2);
        assertChar(1, 24, char1);
    }


    @Test
    public void testSaveSet() {
        buffer.setSafeCursorPosition(new CursorPosition(-7, -3));
        assertPosition(1, 1);
        buffer.setSafeCursorPosition(new CursorPosition(99, 99));
        assertPosition(80, 24);
        buffer.setSafeCursorPosition(new CursorPosition(5, 200));
        assertPosition(5, 80);
        buffer.setSafeCursorPosition(new CursorPosition(4, 8));
        assertPosition(4, 8);
    }

    // TODO: too long line
}
