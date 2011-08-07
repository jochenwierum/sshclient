package de.jowisoftware.sshclient.terminal.buffer;

import static org.junit.Assert.assertEquals;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class DefaultBufferStorageTest {
    private final Mockery context = new Mockery();
    private DefaultBufferStorage<GfxChar> storage;
    private GfxChar nullChar;
    private GfxChar char1;
    private GfxChar char2;

    @Before
    public void setUp() {
        nullChar = context.mock(GfxChar.class, "nullChar");
        char1 = context.mock(GfxChar.class, "char1");
        char2 = context.mock(GfxChar.class, "char2");
        storage = new DefaultBufferStorage<GfxChar>(nullChar, 80, 24);
    }

    private void assertChar(final int y, final int x, final GfxChar character) {
        assertEquals(character, storage.getCharacterAt(y, x));
    }

    @Test
    public void testInitialValue() {
        assertChar(0, 0, nullChar);
        assertChar(23, 79, nullChar);
    }

    @Test
    public void testGetSet() {
        storage.setCharacter(2, 3, char1);
        storage.setCharacter(3, 2, char2);
        assertChar(2, 3, char1);
        assertChar(3, 2, char2);
        assertChar(3, 3, nullChar);
    }

    @Test
    public void testResize() {
        storage.setCharacter(0, 0, char1);
        storage.setCharacter(23, 23, char2);
        storage.newSize(24, 30);

        assertChar(0, 0, char1);
        assertChar(23, 23, char2);
        assertChar(29, 23, nullChar);

        storage.setCharacter(29, 23, char1);
        assertChar(29, 23, char1);
    }

    @Test
    public void testRollDown() {
        storage.setCharacter(0, 0, char2);
        storage.setCharacter(1, 0, char1);
        storage.setCharacter(2, 0, char2);
        storage.setCharacter(3, 0, char2);
        storage.setCharacter(4, 0, char1);
        storage.setCharacter(5, 0, char2);

        storage.shiftLines(2, 1, 5);

        assertChar(0, 0, char2);
        assertChar(1, 0, nullChar);
        assertChar(2, 0, nullChar);
        assertChar(3, 0, char1);
        assertChar(4, 0, char2);
        assertChar(5, 0, char2);
        assertChar(6, 0, nullChar);

        storage.shiftLines(1, 4, 24);

        assertChar(0, 0, char2);
        assertChar(1, 0, nullChar);
        assertChar(2, 0, nullChar);
        assertChar(3, 0, char1);
        assertChar(4, 0, nullChar);
        assertChar(5, 0, char2);
        assertChar(6, 0, char2);
        assertChar(7, 0, nullChar);
    }

    @Test
    public void testRollUp() {
        storage.setCharacter(0, 0, char2);
        storage.setCharacter(1, 0, char1);
        storage.setCharacter(2, 0, char2);
        storage.setCharacter(3, 0, char2);
        storage.setCharacter(4, 0, char1);
        storage.setCharacter(5, 0, char2);

        storage.shiftLines(-2, 1, 5);

        assertChar(0, 0, char2);
        assertChar(1, 0, char2);
        assertChar(2, 0, char1);
        assertChar(3, 0, nullChar);
        assertChar(4, 0, nullChar);
        assertChar(5, 0, char2);

        storage.shiftLines(-1, 0, 1);

        assertChar(0, 0, nullChar);
        assertChar(1, 0, char2);
        assertChar(2, 0, char1);
        assertChar(3, 0, nullChar);
        assertChar(4, 0, nullChar);
        assertChar(5, 0, char2);
        assertChar(23, 0, nullChar);
    }

    @Test
    public void testEraseOneLine() {
        storage.setCharacter(0, 0, char1);
        storage.setCharacter(1, 0, char1);
            storage.setCharacter(1, 1, char1);
            storage.setCharacter(1, 2, char1);
            storage.setCharacter(1, 79, char1);
        storage.setCharacter(2, 0, char1);
        storage.setCharacter(3, 0, char1);
            storage.setCharacter(3, 1, char1);
            storage.setCharacter(3, 1, char1);
            storage.setCharacter(3, 1, char1);
        storage.setCharacter(4, 0, char1);

        storage.erase(new Range(new Position(0, 1),
                new Position(79, 1)));
        storage.erase(new Range(new Position(0, 3),
                new Position(79, 3)));

        assertChar(0, 0, char1);
        assertChar(2, 0, char1);
        assertChar(4, 0, char1);
        assertChar(1, 0, nullChar);
        assertChar(1, 2, nullChar);
        assertChar(3, 0, nullChar);
        assertChar(3, 2, nullChar);
        assertChar(1, 79, nullChar);
    }

    @Test
    public void testEraseAll() {
        storage.setCharacter(0, 0, char1);
        storage.setCharacter(1, 0, char1);
        storage.setCharacter(23, 79, char1);

        storage.erase(new Range(new Position(0, 0),
                new Position(79, 23)));

        assertChar(0, 0, nullChar);
        assertChar(23, 79, nullChar);
        assertChar(1, 0, nullChar);
        assertChar(4, 4, nullChar);
    }

    @Test
    public void testReaseRange() {
        storage.setCharacter(2, 1, char1);
        storage.setCharacter(2, 2, char2);

        storage.setCharacter(4, 5, char2);

        storage.setCharacter(5, 5, char2);
        storage.setCharacter(5, 6, char1);

        storage.erase(new Range(new Position(2, 2),
                new Position(5, 5)));

        assertChar(2, 1, char1);
        assertChar(2, 2, nullChar);
        assertChar(4, 5, nullChar);
        assertChar(5, 5, nullChar);
        assertChar(5, 6, char1);
    }

    @Test
    public void testReaseInLineRange() {
        storage.setCharacter(0, 0, char1);
        storage.setCharacter(0, 1, char2);
        storage.setCharacter(0, 2, char2);
        storage.setCharacter(0, 3, char2);
        storage.setCharacter(0, 4, char1);

        storage.erase(new Range(new Position(1, 0),
                new Position(3, 0)));

        assertChar(0, 0, char1);
        assertChar(0, 1, nullChar);
        assertChar(0, 2, nullChar);
        assertChar(0, 3, nullChar);
        assertChar(0, 4, char1);
    }

    @Test
    public void testSetClearChar() {
        assertChar(0, 0, nullChar);

        final GfxChar nullChar2 = context.mock(GfxChar.class, "nullChar2");
        storage.setClearChar(nullChar2);
        storage.erase(new Range(new Position(2, 2), new Position(2, 2)));
        assertChar(2, 2, nullChar2);
        assertChar(3, 2, nullChar);
        assertChar(1, 2, nullChar);
    }
}
