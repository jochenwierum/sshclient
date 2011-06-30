package de.jowisoftware.ssh.client.terminal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.jowisoftware.ssh.client.terminal.ArrayListBuffer;
import de.jowisoftware.ssh.client.terminal.CursorPosition;
import de.jowisoftware.ssh.client.ui.GfxAwtChar;
import de.jowisoftware.ssh.client.ui.GfxChar;

public class ArrayListBufferTest {
    private ArrayListBuffer<GfxChar> buffer;

    @Before
    public void setUp() {
        buffer = new ArrayListBuffer<GfxChar>();
    }

    @Test
    public void testInitialValue() {
        assertEquals(0, buffer.getCursorPosition().getX());
        assertEquals(0, buffer.getCursorPosition().getY());
    }

    @Test
    public void testCursorPosition() {
        buffer.setCursorPosition(new CursorPosition(2, 4));
        assertEquals(2, buffer.getCursorPosition().getX());
        assertEquals(4, buffer.getCursorPosition().getY());

        buffer.setCursorPosition(new CursorPosition(1, 1));
        assertEquals(1, buffer.getCursorPosition().getX());
        assertEquals(1, buffer.getCursorPosition().getY());
    }

    @Test
    public void testAddChar() {
        final GfxAwtChar character1 = new GfxAwtChar('x', null, null, null, null);

        buffer.addCharacter(character1);
        assertEquals(0, buffer.getCursorPosition().getY());
        assertEquals(1, buffer.getCursorPosition().getX());
        assertEquals(character1, buffer.getCharacter(0, 0));
    }

    @Test
    public void testAddTwoChars() {
        final GfxAwtChar character1 = new GfxAwtChar('x', null, null, null, null);
        final GfxAwtChar character2 = new GfxAwtChar('x', null, null, null, null);

        buffer.addCharacter(character1);
        buffer.addCharacter(character2);
        assertEquals(0, buffer.getCursorPosition().getY());
        assertEquals(2, buffer.getCursorPosition().getX());
        assertEquals(character1, buffer.getCharacter(0, 0));
        assertEquals(character2, buffer.getCharacter(1, 0));
    }

    @Test
    public void testAddNewLineChars() {
        final GfxAwtChar character1 = new GfxAwtChar('x', null, null, null, null);
        final GfxAwtChar character2 = new GfxAwtChar('x', null, null, null, null);

        buffer.addCharacter(character1);
        assertEquals(0, buffer.getCursorPosition().getY());
        assertEquals(character1, buffer.getCharacter(0, 0));

        buffer.addNewLine();
        assertEquals(1, buffer.getCursorPosition().getY());
        assertEquals(0, buffer.getCursorPosition().getX());

        buffer.addCharacter(character2);
        assertEquals(character2, buffer.getCharacter(0, 1));
        assertEquals(1, buffer.getCursorPosition().getY());
        assertEquals(1, buffer.getCursorPosition().getX());
    }

    @Test
    public void testEraseDown() {
        final GfxAwtChar character = new GfxAwtChar('x', null, null, null, null);

        /*
         * delete from y to bottom, including y
         *   01
         * 0 x
         * 1 x
         * 2 xy
         * 3 x
         */

        buffer.addCharacter(character); buffer.addNewLine();
        buffer.addCharacter(character); buffer.addNewLine();
        buffer.addCharacter(character); buffer.addCharacter(character); buffer.addNewLine();
        buffer.addCharacter(character); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(1, 2));
        buffer.eraseToBottom();

        assertEquals(character, buffer.getCharacter(0, 0));
        assertEquals(character, buffer.getCharacter(0, 1));
        assertEquals(character, buffer.getCharacter(0, 2));
        assertNull(buffer.getCharacter(1, 2));
        assertNull(buffer.getCharacter(0, 3));
    }

    @Test
    public void testEraseRestOfLine() {
        final GfxAwtChar character = new GfxAwtChar('x', null, null, null, null);

        /*
         * delete all y
         *   01
         * 0 x
         * 1 xyyy
         * 2 x
         */

        buffer.addCharacter(character); buffer.addNewLine();
        buffer.addCharacter(character); buffer.addCharacter(character);
            buffer.addCharacter(character); buffer.addCharacter(character); buffer.addNewLine();
        buffer.addCharacter(character); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(1, 1));
        buffer.eraseRestOfLine();

        assertEquals(character, buffer.getCharacter(0, 0));
        assertEquals(character, buffer.getCharacter(0, 1));
        assertNull(buffer.getCharacter(1, 1));
        assertEquals(character, buffer.getCharacter(0, 2));
    }

    @Test
    public void testEraseStartOfLine() {
        final GfxAwtChar character = new GfxAwtChar('x', null, null, null, null);

        /*
         * delete all y
         *   0123
         * 0 x
         * 1 yyxx
         * 2 x
         */

        buffer.addCharacter(character); buffer.addNewLine();
        buffer.addCharacter(character); buffer.addCharacter(character);
            buffer.addCharacter(character); buffer.addCharacter(character); buffer.addNewLine();
        buffer.addCharacter(character); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(1, 1));
        buffer.eraseStartOfLine();

        assertEquals(character, buffer.getCharacter(0, 0));
        assertNull(buffer.getCharacter(0, 1));
        assertNull(buffer.getCharacter(1, 1));
        assertEquals(character, buffer.getCharacter(2, 1));
        assertEquals(character, buffer.getCharacter(3, 1));
        assertEquals(character, buffer.getCharacter(0, 2));
    }

    @Test
    public void testErase() {
        final GfxAwtChar character = new GfxAwtChar('x', null, null, null, null);

        /*
         * delete all x
         *   0123
         * 0 x
         * 1 xx
         * 2 x
         */

        buffer.addCharacter(character); buffer.addNewLine();
        buffer.addCharacter(character); buffer.addCharacter(character); buffer.addNewLine();
        buffer.addCharacter(character); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(1, 1));
        buffer.erase();

        assertNull(buffer.getCharacter(0, 0));
        assertNull(buffer.getCharacter(0, 1));
        assertNull(buffer.getCharacter(1, 1));
        assertNull(buffer.getCharacter(0, 2));
    }

    @Test
    public void testEraseLine() {
        final GfxAwtChar character = new GfxAwtChar('x', null, null, null, null);

        /*
         * delete all y
         *   0123
         * 0 x
         * 1 yyy
         * 2 x
         */

        buffer.addCharacter(character); buffer.addNewLine();
        buffer.addCharacter(character); buffer.addCharacter(character);
            buffer.addCharacter(character); buffer.addNewLine();
        buffer.addCharacter(character); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(1, 1));
        buffer.eraseLine();

        assertEquals(character, buffer.getCharacter(0, 0));
        assertNull(buffer.getCharacter(0, 1));
        assertNull(buffer.getCharacter(1, 1));
        assertNull(buffer.getCharacter(2, 1));
        assertEquals(character, buffer.getCharacter(0, 2));
    }

    @Test
    public void testEraseFromTop() {
        final GfxAwtChar character = new GfxAwtChar('x', null, null, null, null);

        /*
         * delete all y
         *   0123
         * 0 y
         * 1 yyx
         * 2 x
         */

        buffer.addCharacter(character); buffer.addNewLine();
        buffer.addCharacter(character); buffer.addCharacter(character);
            buffer.addCharacter(character); buffer.addNewLine();
        buffer.addCharacter(character); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(1, 1));
        buffer.eraseFromTop();

        assertNull(buffer.getCharacter(0, 0));
        assertNull(buffer.getCharacter(0, 1));
        assertNull(buffer.getCharacter(1, 1));
        assertEquals(character, buffer.getCharacter(2, 1));
        assertEquals(character, buffer.getCharacter(0, 2));
    }

    @Test
    public void testEraseFromTop2() {
        final GfxAwtChar character = new GfxAwtChar('x', null, null, null, null);

        buffer.addCharacter(character);
        buffer.setCursorPosition(new CursorPosition(10, 10));
        buffer.eraseFromTop();

        assertNull(buffer.getCharacter(0, 0));
    }
}
