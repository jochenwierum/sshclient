package de.jowisoftware.sshclient.terminal.buffer;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class DefaultBufferTest {
    private final Mockery context = new Mockery();
    private DefaultBuffer<GfxChar> buffer;
    private BufferStorage<GfxChar> storage;
    private BufferStorage<GfxChar> altStorage;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        storage = context.mock(BufferStorage.class);
        altStorage = context.mock(BufferStorage.class, "altStorage");
        buffer = new DefaultBuffer<GfxChar>(storage, altStorage);
    }

    private void prepareSize(final BufferStorage<GfxChar> selectedStorage,
            final int count, final int width, final int height) {
        context.checking(new Expectations() {{
            if (count <= 0) {
                allowing(selectedStorage).size();
            } else {
                exactly(count).of(selectedStorage).size();
            }
            will(returnValue(new Position(width, height)));
        }});
    }

    private void prepareSize(final int count, final int width, final int height) {
        prepareSize(storage, count, width, height);
    }

    private void prepareSize(final int width, final int height) {
        prepareSize(storage, 0, width, height);
    }

    private void prepareShift(final int offset, final int from, final int to) {
        context.checking(new Expectations() {{
            oneOf(storage).shiftLines(offset, from, to);
        }});
    }

    private void prepareChar(final BufferStorage<GfxChar> selectedStorage,
            final int y, final int x, final GfxChar character) {
        context.checking(new Expectations() {{
            oneOf(selectedStorage).setCharacter(y, x, character);
        }});
    }

    private void prepareChar(final int y, final int x, final GfxChar character) {
        prepareChar(storage, y, x, character);
    }

    private void assertPosition(final int y, final int x) {
        assertEquals(new Position(x, y), buffer.getAbsoluteCursorPosition());
    }

    private void assertPositionInRoll(final int y, final int x) {
        assertEquals(new Position(x, y), buffer.getCursorPosition());
    }

    @Test
    public void testCursorPosition() {
        prepareSize(2, 80, 24);
        buffer.setCursorPosition(new Position(2, 4));
        assertPosition(4, 2);

        buffer.setCursorPosition(new Position(1, 1));
        assertPosition(1, 1);
    }

    @Test
    public void testAddChar() {
        final GfxChar character = context.mock(GfxChar.class);
        prepareSize(80, 24);
        prepareChar(0, 0, character);
        buffer.addCharacter(character);
        assertPosition(1, 2);
    }

    @Test
    public void testAddTwoChars() {
        prepareSize(80, 24);
        final GfxChar char1 = context.mock(GfxChar.class, "char1");
        final GfxChar char2 = context.mock(GfxChar.class, "char2");
        prepareChar(0, 0, char1);
        prepareChar(0, 1, char2);

        buffer.addCharacter(char1);
        buffer.addCharacter(char2);
        assertPosition(1, 3);
    }

    @Test
    public void testAddNewLineChars() {
        prepareSize(80, 24);
        final GfxChar char1 = context.mock(GfxChar.class, "char1");
        final GfxChar char2 = context.mock(GfxChar.class, "char2");
        prepareChar(0, 0, char1);
        prepareChar(1, 0, char2);

        buffer.addCharacter(char1);
        assertPosition(1, 2);

        buffer.addNewLine();
        assertPosition(2, 1);

        buffer.addCharacter(char2);
        assertPosition(2, 2);
    }

    @Test
    public void testResize() {
        prepareSize(2, 30, 24);
        context.checking(new Expectations() {{
            oneOf(storage).newSize(30, 24);
            oneOf(altStorage).newSize(30, 24);
        }});
        buffer.setCursorPosition(new Position(10, 11));
        buffer.newSize(30, 24);
        assertPosition(11, 10);

        prepareSize(2, 50, 44);
        context.checking(new Expectations() {{
            oneOf(storage).newSize(50, 44);
            oneOf(altStorage).newSize(50, 44);
        }});
        buffer.setCursorPosition(new Position(80, 10));
        buffer.newSize(50, 44);
        assertPosition(10, 50);
    }

    @Test
    public void testTooLongLineWithoutWrap() {
        buffer.setAutoWrap(false);
        final GfxChar char1 = context.mock(GfxChar.class, "char1");
        final GfxChar char2 = context.mock(GfxChar.class, "char2");
        prepareSize(80, 24);
        prepareChar(0, 79, char1);
        prepareChar(0, 79, char2);

        buffer.setCursorPosition(new Position(80, 1));
        buffer.addCharacter(char1);
        assertPosition(1, 80);
        buffer.addCharacter(char2);
        assertPosition(1, 80);
    }

    @Test
    public void setRollRangedCursorSet() {
        prepareSize(80, 24);

        final GfxChar char1 = context.mock(GfxChar.class, "char1");
        prepareChar(3, 0, char1);

        buffer.setCursorRelativeToMargin(true);
        buffer.setMargin(3, 10);
        assertPosition(3, 1);
        buffer.setCursorPosition(new Position(1, 2));
        buffer.addCharacter(char1);

        assertPositionInRoll(2, 2);
        assertPosition(4, 2);
    }

    @Test
    public void testFullBuffer() {
        prepareSize(80, 24);
        final GfxChar char1 = context.mock(GfxChar.class, "char1");
        final GfxChar char2 = context.mock(GfxChar.class, "char2");
        prepareChar(23, 0, char2);
        prepareShift(-1, 0, 24);
        prepareChar(23, 0, char1);

        buffer.setCursorPosition(new Position(1, 24));
        buffer.addCharacter(char2); buffer.addNewLine();
        buffer.addCharacter(char1);
    }

    @Test
    public void testMoveCursorUpAndRoll() {
        prepareSize(80, 24);
        buffer.setMargin(2, 3);
        assertPosition(1, 1);
        buffer.setCursorPosition(new Position(2, 3));

        buffer.moveCursorUpAndRoll();
        assertPosition(2, 2);

        buffer.setCursorPosition(buffer.getCursorPosition().withX(1));
        prepareShift(1, 1, 3);
        buffer.moveCursorUpAndRoll();
        assertPosition(2, 1);
    }

    @Test
    public void testMoveCursorUpAndRollWithoutRoll() {
        prepareSize(1, 80, 24);
        buffer.setCursorPosition(new Position(2, 3));
        buffer.moveCursorUpAndRoll();
        buffer.moveCursorUpAndRoll();
        buffer.moveCursorUpAndRoll();
        assertPosition(1, 2);
    }

    @Test
    public void testMoveCursorDownAndRoll() {
        prepareSize(80, 24);
        buffer.setMargin(2, 3);
        assertPosition(1, 1);
        buffer.setCursorPosition(new Position(2, 2));

        buffer.moveCursorDownAndRoll(false);
        assertPosition(3, 2);

        prepareShift(-1, 1, 3);
        buffer.moveCursorDownAndRoll(false);
        assertPosition(3, 2);
    }

    @Test
    public void testMoveCursorDownAndRollWithoutRoll() {
        prepareSize(2, 80, 24);
        buffer.setCursorPosition(new Position(2, 23));
        buffer.moveCursorDownAndRoll(false);
        assertPosition(24, 2);
    }

    @Test
    public void testMoveCursorDownAndRollColReset() {
        prepareSize(80, 24);
        buffer.setMargin(2, 3);

        buffer.setCursorPosition(new Position(1, 2));
        buffer.moveCursorDownAndRoll(true);
        assertPosition(3, 1);

        prepareShift(-1, 1, 3);
        buffer.moveCursorDownAndRoll(true);
        assertPosition(3, 1);
    }

    @Test
    public void testMoveCursorDownAndRollWithoutRollAndColReset() {
        prepareSize(2, 80, 24);
        buffer.setCursorPosition(new Position(2, 24));
        prepareShift(-1, 0, 24);
        buffer.moveCursorDownAndRoll(true);
        assertPosition(24, 1);
    }

    @Test
    public void testErase() {
        final Range range1 = new Range(new Position(2, 25));
        final Range range2 = new Range(new Position(7, 5));

        context.checking(new Expectations(){{
            oneOf(storage).erase(range1.offset(-1, -1));
            oneOf(storage).erase(range2.offset(-1, -1));
        }});

        buffer.erase(range1);
        buffer.erase(range2);
    }

    @Test
    public void testInsertOneLine() {
        prepareSize(2, 80, 24);
        prepareShift(1, 1, 24);
        buffer.setCursorPosition(new Position(5, 2));
        buffer.insertLines(1);
    }

    @Test
    public void testInsertTwoLines() {
        prepareSize(2, 80, 24);
        prepareShift(2, 4, 24);
        buffer.setCursorPosition(new Position(1, 5));
        buffer.insertLines(2);
    }

    @Test
    public void testSetCursorInMargin() {
        prepareSize(80, 24);
        buffer.setMargin(4, 23);
        buffer.setCursorPosition(new Position(3, 3));
        assertEquals(new Position(3, 3),
                buffer.getAbsoluteCursorPosition());

        buffer.setCursorRelativeToMargin(true);
        buffer.setMargin(4, 23);
        assertPosition(4, 1);

        buffer.setCursorPosition(new Position(5, 3));
        assertEquals(new Position(5, 6),
                buffer.getAbsoluteCursorPosition());

        buffer.setCursorRelativeToMargin(false);
        buffer.setCursorPosition(new Position(1, 1));
        assertEquals(new Position(1, 1),
                buffer.getAbsoluteCursorPosition());
    }

    @Test
    public void testLongLineWithWrap() {
        final GfxChar character = context.mock(GfxChar.class);

        buffer.setAutoWrap(true);
        prepareSize(80, 24);

        buffer.setCursorPosition(new Position(80, 1));

        prepareChar(0, 79, character);
        buffer.addCharacter(character);
        assertPosition(1, 80);

        prepareChar(1, 0, character);
        buffer.addCharacter(character);
        assertPosition(2, 2);
    }

    @Test
    public void testLongLineWithWrapAndRoll() {
        final GfxChar character = context.mock(GfxChar.class);

        buffer.setAutoWrap(true);
        prepareSize(80, 24);

        buffer.setCursorPosition(new Position(80, 24));

        prepareShift(-1, 0, 24);
        prepareChar(23, 79, character);
        buffer.addCharacter(character);
        prepareChar(23, 0, character);
        buffer.addCharacter(character);
        assertPosition(24, 2);
    }

    @Test
    public void testLongLineWithBackspace() {
        prepareSize(80, 24);
        buffer.setAutoWrap(true);
        buffer.setCursorPosition(new Position(1, 2));
        buffer.processBackspace();
        assertPosition(1, 80);

        buffer.setAutoWrap(false);
        buffer.setCursorPosition(new Position(1, 2));
        buffer.processBackspace();
        assertPosition(2, 1);
    }

    @Test
    public void testNoWrongWrap() {
        final GfxChar character = context.mock(GfxChar.class);
        prepareSize(80, 24);
        buffer.setCursorPosition(new Position(80, 1));

        prepareChar(0, 79, character);
        assertPosition(1, 80);
        buffer.addCharacter(character);
        assertPosition(1, 80);

        buffer.setCursorPosition(new Position(80, 1));
        prepareChar(0, 79, character);
        assertPosition(1, 80);
        buffer.addCharacter(character);
        assertPosition(1, 80);

        prepareChar(1, 0, character);
        buffer.addCharacter(character);

        prepareChar(0, 79, character);
        buffer.setCursorPosition(new Position(80,1));
        buffer.addCharacter(character);
        assertPosition(1, 80);
        buffer.processBackspace();
        assertPosition(1, 79);
    }

    @Test
    public void testSaveRestore() {
        prepareSize(80, 24);

        buffer.setCursorPosition(new Position(22, 5));
        buffer.saveCursorPosition();
        assertPosition(5, 22);
        buffer.setCursorPosition(new Position(1, 1));

        buffer.restoreCursorPosition();
        assertPosition(5, 22);

        buffer.setCursorPosition(new Position(7, 7));
        buffer.saveCursorPosition();
        buffer.setCursorPosition(new Position(9, 9));
        buffer.restoreCursorPosition();
        assertPosition(7, 7);
    }

    @Test
    public void testSwitchBuffer() {
        final GfxChar gfxChar = context.mock(GfxChar.class);
        prepareSize(80, 24);
        prepareSize(altStorage, 0, 80, 24);

        assertEquals(BufferSelection.PRIMARY, buffer.getSelectedBuffer());
        buffer.switchBuffer(BufferSelection.ALTERNATIVE);
        assertEquals(BufferSelection.ALTERNATIVE, buffer.getSelectedBuffer());
        buffer.setCursorPosition(new Position(2, 2));

        prepareChar(altStorage, 1, 1, gfxChar);
        buffer.addCharacter(gfxChar);

        buffer.switchBuffer(BufferSelection.PRIMARY);
        assertEquals(BufferSelection.PRIMARY, buffer.getSelectedBuffer());
        prepareChar(1, 2, gfxChar);
        buffer.addCharacter(gfxChar);
    }

    @Test
    public void testClearChar() {
        final GfxChar gfxChar = context.mock(GfxChar.class);
        context.checking(new Expectations() {{
            oneOf(storage).setClearChar(gfxChar);
        }});
        buffer.setClearChar(gfxChar);
    }

    private void testShift(final int x, final int y, final int count) {
        prepareSize(80, 60);
        buffer.setCursorPosition(new Position(x, y));

        context.checking(new Expectations() {{
            oneOf(storage).shiftColumns(count, x - 1, y - 1);
        }});

        buffer.shift(count);
    }

    @Test
    public void rightShift3() {
        testShift(3, 4, 3);
    }

    @Test
    public void rightShiftMinus6() {
        testShift(5, 1, -6);
    }
}