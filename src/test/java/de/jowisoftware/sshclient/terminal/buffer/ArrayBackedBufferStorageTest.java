package de.jowisoftware.sshclient.terminal.buffer;

import de.jowisoftware.sshclient.terminal.gfx.GfxChar;
import de.jowisoftware.sshclient.util.RingBuffer;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

public class ArrayBackedBufferStorageTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private ArrayBackedBufferStorage storage;
    private GfxChar nullChar;
    private GfxChar char1;
    private GfxChar char2;

    // TODO: add Tests for storage
    private RingBuffer<GfxChar[]> history;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        nullChar = context.mock(GfxChar.class, "nullChar");
        char1 = context.mock(GfxChar.class, "char1");
        char2 = context.mock(GfxChar.class, "char2");
        history = context.mock(RingBuffer.class, "history");
        storage = new ArrayBackedBufferStorage(nullChar, 80, 24, history);

        context.checking(new Expectations() {{
            allowing(history);
            allowing(char1).getCharCount(); will(returnValue(1));
            allowing(char2).getCharCount(); will(returnValue(1));
        }});
    }

    private void assertChar(final int y, final int x, final GfxChar character) {
        assertThat(storage.getCharacterAt(y, x), is(character));
    }

    @Test
    public void initialValues() {
        assertChar(0, 0, nullChar);
        assertChar(23, 79, nullChar);
    }

    @Test
    public void setAndGetChars() {
        storage.setCharacter(2, 3, char1);
        storage.setCharacter(3, 2, char2);
        assertChar(2, 3, char1);
        assertChar(3, 2, char2);
        assertChar(3, 3, nullChar);
    }

    @Test
    public void resizeTo24x30() {
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
    public void rollDown() {
        final GfxChar clearChar2 = context.mock(GfxChar.class, "clearChar2");
        storage.setClearChar(clearChar2);

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
    public void rollUp() {
        final GfxChar clearChar2 = context.mock(GfxChar.class, "clearChar2");
        storage.setClearChar(clearChar2);

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
    public void eraseOneLine() {
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
    public void eraseAll() {
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
    public void eraseRange() {
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
    public void eraseInLineRange() {
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
    public void setClearCharAndErase() {
        assertChar(0, 0, nullChar);

        final GfxChar nullChar2 = context.mock(GfxChar.class, "nullChar2");
        storage.setClearChar(nullChar2);
        storage.erase(new Range(new Position(2, 2), new Position(2, 2)));
        assertChar(2, 2, nullChar2);
        assertChar(3, 2, nullChar);
        assertChar(1, 2, nullChar);
    }

    @Test
    public void shiftLeft3Columns() {
        storage.newSize(11, 6);

        storage.setCharacter(4, 3, char1);
        storage.setCharacter(4, 4, char1);
        storage.setCharacter(4, 5, char1);
        storage.setCharacter(4, 6, char1);
        storage.setCharacter(4, 7, char2);
        storage.setCharacter(4, 8, char1);
        storage.setCharacter(4, 9, char1);
        storage.setCharacter(4, 10, char2);

        storage.shiftColumns(3, 4, 4);

        assertChar(4, 3, char1);
        assertChar(4, 4, char2);
        assertChar(4, 5, char1);
        assertChar(4, 6, char1);
        assertChar(4, 7, char2);
        assertChar(4, 8, nullChar);
        assertChar(4, 10, nullChar);
    }

    @Test
    public void shiftLeft1Column() {
        storage.newSize(8, 6);

        storage.setCharacter(3, 3, char1);
        storage.setCharacter(3, 4, char2);
        storage.setCharacter(3, 5, char1);
        storage.setCharacter(3, 6, char2);
        storage.setCharacter(3, 7, char1);

        storage.shiftColumns(1, 5, 3);

        assertChar(3, 3, char1);
        assertChar(3, 4, char2);
        assertChar(3, 5, char2);
        assertChar(3, 6, char1);
        assertChar(3, 7, nullChar);
    }

    @Test
    public void shiftRight1Column() {
        storage.newSize(8, 6);

        storage.setCharacter(3, 3, char1);
        storage.setCharacter(3, 4, char2);
        storage.setCharacter(3, 5, char1);
        storage.setCharacter(3, 6, char2);
        storage.setCharacter(3, 7, char1);

        storage.shiftColumns(-1, 4, 3);

        assertChar(3, 3, char1);
        assertChar(3, 4, nullChar);
        assertChar(3, 5, char2);
        assertChar(3, 6, char1);
        assertChar(3, 7, char2);
    }

    @Test
    public void shiftRight3Columns() {
        storage.newSize(8, 6);

        storage.setCharacter(3, 3, char1);
        storage.setCharacter(3, 4, char2);
        storage.setCharacter(3, 5, char1);
        storage.setCharacter(3, 6, char2);
        storage.setCharacter(3, 7, char1);

        storage.shiftColumns(-3, 4, 3);

        assertChar(3, 3, char1);
        assertChar(3, 4, nullChar);
        assertChar(3, 5, nullChar);
        assertChar(3, 6, nullChar);
        assertChar(3, 7, char2);
    }

    @Test
    public void multiByteCharsMarkFollowingFieldsAsEmpty() {
        final GfxChar gfxChar = context.mock(GfxChar.class);

        context.checking(new Expectations() {{
            allowing(gfxChar).getCharCount(); will(returnValue(2));
        }});

        storage.setCharacter(2, 3, gfxChar);

        assertThat(storage.getCharacterAt(2, 3),
                is(sameInstance(gfxChar)));

        assertThat(storage.getCharacterAt(2, 4),
                is(sameInstance(BufferStorage.EMPTY)));

        assertThat(storage.getCharacterAt(2, 5),
                is(sameInstance(nullChar)));
    }

    @Test public void
    shiftMoreLinesUpThanAvailable() {
        storage.setCharacter(9, 0, char2);
        storage.setCharacter(10, 0, char1);
        storage.setCharacter(11, 0, char1);
        storage.setCharacter(12, 0, char2);

        storage.shiftLines(-15, 10, 12);

        assertChar(9, 0, char2);
        assertChar(10, 0, nullChar);
        assertChar(11, 0, nullChar);
        assertChar(12, 0, char2);
    }

    @Test public void
    shiftMoreLinesDownThanAvailable() {
        storage.setCharacter(9, 0, char2);
        storage.setCharacter(10, 0, char1);
        storage.setCharacter(11, 0, char1);
        storage.setCharacter(12, 0, char2);

        storage.shiftLines(60, 10, 12);

        assertChar(9, 0, char2);
        assertChar(10, 0, nullChar);
        assertChar(11, 0, nullChar);
        assertChar(12, 0, char2);
    }
}
