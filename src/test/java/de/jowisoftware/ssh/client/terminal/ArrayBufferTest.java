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
        assertPosition(0, 0);
        assertChar(0, 0, nullChar);
        assertChar(79, 23, nullChar);
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
        assertPosition(1, 0);
        assertChar(0, 0, char1);
    }

    @Test
    public void testAddTwoChars() {
        buffer.addCharacter(char1);
        buffer.addCharacter(char2);
        assertPosition(2, 0);
        assertChar(0, 0, char1);
        assertChar(1, 0, char2);
    }

    @Test
    public void testAddNewLineChars() {
        buffer.addCharacter(char1);
        assertPosition(1, 0);

        buffer.addNewLine();
        assertPosition(0, 1);

        buffer.addCharacter(char2);
        assertChar(0, 1, char2);
        assertPosition(1, 1);
    }

    @Test
    public void testEraseDown() {
        /*
         * delete from y to bottom, including y
         *   01
         * 0 x
         * 1 x
         * 2 xy
         * 3 x
         */

        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char2); buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char2); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(1, 2));
        buffer.eraseToBottom();

        assertChar(0, 0, char1);
        assertChar(0, 1, char1);
        assertChar(0, 2, char2);
        assertChar(1, 2, nullChar);
        assertChar(0, 3, nullChar);
    }

    @Test
    public void testEraseRestOfLine() {
        /*
         * delete all y
         *   01
         * 0 x
         * 1 xyyy
         * 2 x
         */

        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addCharacter(char2);
            buffer.addCharacter(char1); buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char2); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(1, 1));
        buffer.eraseRestOfLine();

        assertChar(0, 0, char1);
        assertChar(0, 1, char1);
        assertChar(1, 1, nullChar);
        assertChar(1, 2, nullChar);
        assertChar(1, 3, nullChar);
        assertChar(0, 2, char2);
    }

    @Test
    public void testEraseStartOfLine() {
        /*
         * delete all y
         *   0123
         * 0 x
         * 1 yyxx
         * 2 x
         */

        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addCharacter(char1);
            buffer.addCharacter(char1); buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(1, 1));
        buffer.eraseStartOfLine();

        assertChar(0, 0, char1);
        assertChar(0, 1, nullChar);
        assertChar(1, 1, nullChar);
        assertChar(2, 1, char1);
        assertChar(3, 1, char1);
        assertChar(0, 2, char1);
    }

    @Test
    public void testErase() {
        /*
         * delete all x
         *   0123
         * 0 x
         * 1 xx
         * 2 x
         */

        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(1, 1));
        buffer.erase();

        assertChar(0, 0, nullChar);
        assertChar(0, 1, nullChar);
        assertChar(1, 1, nullChar);
        assertChar(0, 2, nullChar);
    }

    @Test
    public void testEraseLine() {
        /*
         * delete all y
         *   0123
         * 0 x
         * 1 yyy
         * 2 x
         */

        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addCharacter(char1);
            buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(1, 1));
        buffer.eraseLine();

        assertChar(0, 0, char1);
        assertChar(0, 1, nullChar);
        assertChar(1, 1, nullChar);
        assertChar(2, 1, nullChar);
        assertChar(0, 2, char1);
    }

    @Test
    public void testEraseFromTop() {
        /*
         * delete all y
         *   0123
         * 0 y
         * 1 yyx
         * 2 x
         */

        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addCharacter(char1);
            buffer.addCharacter(char1); buffer.addNewLine();
        buffer.addCharacter(char1); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(1, 1));
        buffer.eraseFromTop();

        assertChar(0, 0, nullChar);
        assertChar(0, 1, nullChar);
        assertChar(1, 1, nullChar);
        assertChar(2, 1, char1);
        assertChar(0, 2, char1);
    }

    @Test
    public void testResize() {
        buffer.addCharacter(char1);
        buffer.setCursorPosition(new CursorPosition(23, 23));
        buffer.addCharacter(char2);
        buffer.newSize(24, 31);

        assertChar(0, 0, char1);
        assertChar(23, 23, char2);
        assertChar(23, 30, nullChar);

        buffer.setCursorPosition(new CursorPosition(23, 30));
        buffer.addCharacter(char1);
        assertChar(23, 30, char1);
    }

    @Test
    public void testTooLongLine() {
        buffer.setCursorPosition(new CursorPosition(79, 0));
        buffer.addCharacter(char1);
        assertPosition(0, 1);
        buffer.addCharacter(char2);
        assertPosition(1, 1);
    }
    // TODO: too long line, too full buffer
}
