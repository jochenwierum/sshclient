package de.jowisoftware.ssh.client.tty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.jowisoftware.ssh.client.ui.GfxAwtChar;
import de.jowisoftware.ssh.client.ui.GfxChar;

public class ArrayListBufferTest {
    private Buffer<GfxChar> buffer;

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

        buffer.addCharacter(character); buffer.addNewLine();
        buffer.addCharacter(character); buffer.addNewLine();
        buffer.addCharacter(character); buffer.addNewLine();
        buffer.addCharacter(character); buffer.addNewLine();
        buffer.setCursorPosition(new CursorPosition(0, 2));
        buffer.eraseDown();

        assertNull(buffer.getCharacter(0, 2));
        assertNull(buffer.getCharacter(0, 3));
        assertEquals(character, buffer.getCharacter(0, 1));
        assertEquals(character, buffer.getCharacter(0, 0));
    }
}
