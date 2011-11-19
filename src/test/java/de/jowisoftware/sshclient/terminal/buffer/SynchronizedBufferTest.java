package de.jowisoftware.sshclient.terminal.buffer;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class SynchronizedBufferTest {
    private final Mockery context = new JUnit4Mockery();
    private SynchronizedBuffer buffer;
    private FlippableBufferStorage storage;
    private TabStopManager tabstops;
    private CursorPositionManager positionManager;

    @Before
    public void setUp() {
        storage = context.mock(FlippableBufferStorage.class, "storage");
        tabstops = context.mock(TabStopManager.class, "tabstops");
        positionManager = context.mock(CursorPositionManager.class);
        buffer = new SynchronizedBuffer(storage, tabstops, positionManager);
    }

    private void allowSize(final int width, final int height) {
        context.checking(new Expectations() {{
            allowing(storage).size();
            will(returnValue(new Position(width, height)));
        }});
    }

    private void prepareShift(final int offset, final int from, final int to) {
        context.checking(new Expectations() {{
            oneOf(storage).shiftLines(offset, from, to);
        }});
    }

    private void assertCharWillBeSet(
            final int y, final int x, final GfxChar character) {
        context.checking(new Expectations() {{
            oneOf(storage).setCharacter(y - 1, x - 1, character);
        }});
    }

    private void allowPosition(final int column, final int row) {
        context.checking(new Expectations() {{
            allowing(positionManager).currentPositionInScreen();
                will(returnValue(new Position(row, column)));
        }});
    }

    private void allowWordWrap(final boolean allowIt) {
        context.checking(new Expectations() {{
            allowing(positionManager).wouldWrap();
                will(returnValue(allowIt));
            allowing(positionManager).resetWouldWrap();
        }});
    }

    private void assertCursorWillBeMovedToNextPosition() {
        context.checking(new Expectations() {{
            oneOf(positionManager).moveToNextPosition(1);
        }});
    }

    private void allowMargineDefined(final boolean isDefined) {
        context.checking(new Expectations() {{
            allowing(positionManager).isMarginDefined(); will(returnValue(isDefined));
        }});
    }

    private void expectPosition(final int howOften, final int y, final int x) {
        context.checking(new Expectations() {{
            exactly(howOften).of(positionManager).currentPositionInScreen();
                will(returnValue(new Position(x, y)));
        }});
    }

    @Test
    public void addCharAt00ChangesPositionAndAddsChar() {
        final GfxChar character = context.mock(GfxChar.class);
        allowSize(80, 24);
        allowPosition(1, 1);
        allowWordWrap(true);

        context.checking(new Expectations() {{
            oneOf(character).getCharCount(); will(returnValue(1));
        }});

        assertCharWillBeSet(1, 1, character);
        assertCursorWillBeMovedToNextPosition();
        buffer.addCharacter(character);
    }

    @Test
    public void addCharAt42ChangesPositionAndAddsChar() {
        final GfxChar character = context.mock(GfxChar.class);
        allowSize(80, 24);
        allowPosition(4, 2);
        allowWordWrap(true);

        context.checking(new Expectations() {{
            oneOf(character).getCharCount(); will(returnValue(1));
        }});

        assertCharWillBeSet(4, 2, character);
        assertCursorWillBeMovedToNextPosition();
        buffer.addCharacter(character);
    }

    @Test
    public void addNewLineChangesPosition() {
        allowSize(80, 24);

        context.checking(new Expectations(){{
            oneOf(positionManager).moveDownAndRoll();
            allowing(positionManager).currentPositionInScreen();
                will(returnValue(new Position(3, 7)));
            oneOf(positionManager).setPositionSafelyInScreen(new Position(1, 7));
        }});

        buffer.moveCursorDown(true);
    }

    @Test
    public void resizeTo30x24ResizesChildren() {
        context.checking(new Expectations() {{
            oneOf(storage).newSize(30, 24);
            oneOf(positionManager).newSize(30, 24);
        }});
        buffer.newSize(30, 24);
    }

    @Test
    public void resizeTo50x44ResizesChildren() {
        context.checking(new Expectations() {{
            oneOf(storage).newSize(50, 44);
            oneOf(positionManager).newSize(50, 44);
        }});
        buffer.newSize(50, 44);
    }

    @Test
    public void tooLongLinesAreNotWrapped() {
        buffer.setAutoWrap(false);
        final GfxChar char1 = context.mock(GfxChar.class, "char1");
        final GfxChar char2 = context.mock(GfxChar.class, "char2");
        allowSize(80, 24);

        expectPosition(2, 1, 80);
        assertCharWillBeSet(1, 80, char1);
        context.checking(new Expectations() {{
            allowing(positionManager).resetWouldWrap();
            allowing(positionManager).moveToNextPosition(1);
            allowing(char1).getCharCount(); will(returnValue(1));
        }});
        buffer.addCharacter(char1);

        expectPosition(2, 4, 80);
        assertCharWillBeSet(4, 80, char2);
        context.checking(new Expectations() {{
            allowing(positionManager).resetWouldWrap();
            allowing(positionManager).moveToNextPosition(1);
            allowing(char2).getCharCount(); will(returnValue(1));
        }});
        buffer.addCharacter(char2);
    }

    @Test
    public void setRollRangedCursorSet() {
        allowSize(80, 24);
        buffer.setCursorRelativeToMargin(true);

        final States marginSet = context.states("state");

        context.checking(new Expectations() {{
            oneOf(positionManager).setMargins(3, 10);
                then(marginSet.is("with-margin"));
            oneOf(positionManager).setPositionSafelyInMargin(new Position(1, 1));
                when(marginSet.is("with-margin"));

            allowing(positionManager).isMarginDefined();
                will(returnValue(true));
                when(marginSet.is("with-margin"));
        }});
        buffer.setMargin(3, 10);

        context.checking(new Expectations() {{
            oneOf(positionManager).setPositionSafelyInMargin(new Position(1, 2));
        }});
        buffer.setCursorPosition(new Position(1, 2));
    }

    @Test
    public void moveCursorUpAndRollIsForwarded() {
        context.checking(new Expectations() {{
            oneOf(positionManager).moveUpAndRoll();
        }});
        buffer.moveCursor();
    }

    @Test
    public void setMargin2to3IsForwarded() {
        checkMarginForward(2, 3);
    }

    @Test
    public void setMargin5to9IsForwarded() {
        checkMarginForward(5, 9);
    }

    public void checkMarginForward(final int x, final int y) {
        final States marginSet = context.states("marginSet");
        context.checking(new Expectations() {{
            oneOf(positionManager).setMargins(x, y);
                then(marginSet.is("margin-set"));
            oneOf(positionManager).setPositionSafelyInMargin(new Position(1, 1));
                when(marginSet.is("margin-set"));
        }});
        buffer.setMargin(x, y);
    }

    @Test
    public void moveCursorDownAndRollIsForwarded() {
        allowSize(80, 24);

        context.checking(new Expectations() {{
            oneOf(positionManager).moveDownAndRoll();
        }});

        buffer.moveCursorDown(false);
    }

    @Test
    public void moveCursorDownResetsColumnAtLine7() {
        checkMoveCursorDown(new Position(3, 7));
    }

    @Test
    public void moveCursorDownResetsColumnAtLine3() {
        checkMoveCursorDown(new Position(6, 3));
    }

    public void checkMoveCursorDown(final Position pos) {
        context.checking(new Expectations() {{
            oneOf(positionManager).moveDownAndRoll();
            oneOf(positionManager).currentPositionInScreen();
                will(returnValue(pos));
            oneOf(positionManager).setPositionSafelyInScreen(pos.withX(1));
        }});

        buffer.moveCursorDown(true);
    }

    @Test
    public void moveCursorDownAndRollDoesNotResetColumn() {
        context.checking(new Expectations() {{
            oneOf(positionManager).moveDownAndRoll();
        }});

        buffer.moveCursorDown(false);
    }

    @Test
    public void erase2to25() {
        final Range range = new Range(new Position(2, 25));

        context.checking(new Expectations(){{
            oneOf(storage).erase(range.offset(-1, -1));
        }});

        buffer.erase(range);
    }

    @Test
    public void erase7to5() {
        final Range range = new Range(new Position(7, 5));

        context.checking(new Expectations(){{
            oneOf(storage).erase(range.offset(-1, -1));
        }});

        buffer.erase(range);
    }

    @Test
    public void insertOneLineWithoutMargin() {
        allowSize(80, 24);
        prepareShift(1, 1, 24);
        allowMargineDefined(false);

        context.checking(new Expectations() {{
            oneOf(positionManager).currentPositionInScreen();
                will(returnValue(new Position(7, 2)));
        }});

        buffer.insertLines(1);
    }

    @Test
    public void insertTwoLinesWithoutMargin() {
        allowSize(80, 24);
        prepareShift(2, 4, 24);
        allowMargineDefined(false);

        context.checking(new Expectations() {{
            oneOf(positionManager).currentPositionInScreen();
                will(returnValue(new Position(1, 5)));
        }});

        buffer.insertLines(2);
    }


    @Test
    public void insertOneLineWithMargin() {
        allowSize(80, 24);
        prepareShift(1, 9, 20);
        allowMargineDefined(true);

        context.checking(new Expectations() {{
            allowing(positionManager).currentPositionInScreen();
                will(returnValue(new Position(7, 10)));

            allowing(positionManager).getBottomMargin();
                will(returnValue(20));
        }});

        buffer.insertLines(1);
    }

    @Test
    public void insertTwoLinesWithMargin() {
        allowSize(80, 24);
        prepareShift(2, 4, 21);
        allowMargineDefined(true);

        context.checking(new Expectations() {{
            oneOf(positionManager).currentPositionInScreen();
                will(returnValue(new Position(1, 5)));
            allowing(positionManager).getBottomMargin();
                will(returnValue(21));
        }});

        buffer.insertLines(2);
    }

    @Test
    public void setCursorInMargin() {
        buffer.setCursorRelativeToMargin(true);
        context.checking(new Expectations() {{
            oneOf(positionManager).isMarginDefined();
                will(returnValue(true));
            oneOf(positionManager).setPositionSafelyInMargin(new Position(3, 3));
        }});
        buffer.setCursorPosition(new Position(3, 3));

        context.checking(new Expectations() {{
            oneOf(positionManager).isMarginDefined();
                will(returnValue(true));
            oneOf(positionManager).setPositionSafelyInMargin(new Position(5, 3));
        }});
        buffer.setCursorPosition(new Position(5, 3));

        buffer.setCursorRelativeToMargin(false);
        context.checking(new Expectations() {{
            oneOf(positionManager).isMarginDefined();
                will(returnValue(true));
            oneOf(positionManager).setPositionSafelyInScreen(new Position(3, 3));
        }});
        buffer.setCursorPosition(new Position(3, 3));
    }

    @Test
    public void setCursorInScreen() {
        context.checking(new Expectations() {{
            oneOf(positionManager).isMarginDefined();
                will(returnValue(false));
            oneOf(positionManager).setPositionSafelyInScreen(new Position(3, 3));
        }});
        buffer.setCursorPosition(new Position(3, 3));

        context.checking(new Expectations() {{
            oneOf(positionManager).isMarginDefined();
                will(returnValue(false));
            oneOf(positionManager).setPositionSafelyInScreen(new Position(5, 3));
        }});

        buffer.setCursorPosition(new Position(5, 3));
    }

    @Test
    public void longLinesWillWrap() {
        final GfxChar character = context.mock(GfxChar.class, "character");

        buffer.setAutoWrap(true);
        allowSize(80, 24);

        context.checking(new Expectations() {{
            oneOf(positionManager).currentPositionInScreen();
                will(returnValue(new Position(80, 1)));
            allowing(positionManager).wouldWrap(); will(returnValue(true));
            oneOf(positionManager).resetWouldWrap();

            oneOf(positionManager).setPositionSafelyInScreen(new Position(1, 2));
            oneOf(positionManager).currentPositionInScreen();
                will(returnValue(new Position(1, 2)));
            oneOf(positionManager).moveToNextPosition(1);

            allowing(character).getCharCount(); will(returnValue(1));
        }});

        assertCharWillBeSet(2, 1, character);
        buffer.addCharacter(character);
    }

    @Test
    public void testLongLineWithBackspace() {
        allowSize(80, 24);

        buffer.setAutoWrap(true);
        context.checking(new Expectations() {{
            oneOf(positionManager).currentPositionInScreen();
                will(returnValue(new Position(1, 2)));
            oneOf(positionManager).setPositionSafelyInScreen(new Position(80, 1));
        }});
        buffer.processBackspace();

        buffer.setAutoWrap(false);
        context.checking(new Expectations() {{
            oneOf(positionManager).currentPositionInScreen();
                will(returnValue(new Position(1, 1)));
            oneOf(positionManager).setPositionSafelyInScreen(new Position(0, 1));
        }});
        buffer.processBackspace();
    }

    @Test
    public void noWrongWrap() {
        final GfxChar character = context.mock(GfxChar.class);
        allowSize(80, 24);

        context.checking(new Expectations() {{
            allowing(positionManager).currentPositionInScreen();
                will(returnValue(new Position(80, 1)));
            oneOf(positionManager).wouldWrap(); will(returnValue(false));
            oneOf(positionManager).resetWouldWrap();
            oneOf(positionManager).moveToNextPosition(1);
            allowing(character).getCharCount(); will(returnValue(1));
        }});

        assertCharWillBeSet(1, 80, character);
        buffer.addCharacter(character);
    }

    @Test
    public void saveAndRestoreAreForwarded() {
        context.checking(new Expectations() {{
            oneOf(positionManager).save();
        }});
        buffer.saveCursorPosition();

        context.checking(new Expectations() {{
            oneOf(positionManager).restore();
        }});
        buffer.restoreCursorPosition();
    }

    @Test
    public void switchBufferToPrimaryBufferSwitchesBuffer() {
        context.checking(new Expectations() {{
            oneOf(storage).getSelectedStorage();
                will(returnValue(BufferSelection.PRIMARY));
        }});

        assertEquals(BufferSelection.PRIMARY, buffer.getSelectedBuffer());

        context.checking(new Expectations() {{
            oneOf(storage).flipTo(BufferSelection.PRIMARY);
        }});
        buffer.switchBuffer(BufferSelection.PRIMARY);
    }

    @Test
    public void switchBufferToAlternativeBufferSwitchesBuffer() {
        context.checking(new Expectations() {{
            oneOf(storage).getSelectedStorage();
                will(returnValue(BufferSelection.ALTERNATIVE));
        }});

        assertEquals(BufferSelection.ALTERNATIVE, buffer.getSelectedBuffer());

        context.checking(new Expectations() {{
            oneOf(storage).flipTo(BufferSelection.ALTERNATIVE);
        }});
        buffer.switchBuffer(BufferSelection.ALTERNATIVE);
    }

    @Test
    public void clearCharIsForwarded() {
        final GfxChar gfxChar = context.mock(GfxChar.class);
        context.checking(new Expectations() {{
            oneOf(storage).setClearChar(gfxChar);
        }});
        buffer.setClearChar(gfxChar);
    }

    private void testShift(final int x, final int y, final int count) {
        context.checking(new Expectations() {{
            oneOf(positionManager).currentPositionInScreen();
                will(returnValue(new Position(x, y)));
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

    @Test
    public void horizontalTabulatorMovesCuros1() {
        allowSize(80, 24);
        final Position pos = new Position(2, 3);
        final Position pos2 = new Position(5, 7);

        assertTabulatorTransition(pos, pos2);
    }

    @Test
    public void horizontalTabulatorMovesCuros2() {
        allowSize(80, 24);
        final Position pos = new Position(9, 5);
        final Position pos2 = new Position(8, 4);

        assertTabulatorTransition(pos, pos2);
    }

    private void assertTabulatorTransition(final Position pos,
            final Position pos2) {
        context.checking(new Expectations() {{
            oneOf(positionManager).currentPositionInScreen();
                will(returnValue(pos));
            oneOf(tabstops).getNextHorizontalTabPosition(pos);
                will(returnValue(pos2));
            allowing(positionManager).isMarginDefined();
                will(returnValue(true));
            oneOf(positionManager).setPositionSafelyInScreen(pos2);
        }});

        buffer.tabulator(TabulatorOrientation.HORIZONTAL);
    }

    @Test
    public void removeOneLineWithoutMargin() {
        allowSize(80, 24);
        prepareShift(-1, 1, 24);
        allowMargineDefined(false);

        context.checking(new Expectations() {{
            oneOf(positionManager).currentPositionInScreen();
                will(returnValue(new Position(7, 2)));
        }});

        buffer.removeLines(1);
    }

    @Test
    public void removeTwoLinesWithoutMargin() {
        allowSize(80, 24);
        prepareShift(-2, 4, 24);
        allowMargineDefined(false);

        context.checking(new Expectations() {{
            oneOf(positionManager).currentPositionInScreen();
                will(returnValue(new Position(1, 5)));
        }});

        buffer.removeLines(2);
    }

    @Test
    public void removeOneLineWithMargin() {
        allowSize(80, 24);
        prepareShift(-1, 9, 20);
        allowMargineDefined(true);

        context.checking(new Expectations() {{
            allowing(positionManager).currentPositionInScreen();
                will(returnValue(new Position(7, 10)));

            allowing(positionManager).getBottomMargin();
                will(returnValue(20));
        }});

        buffer.removeLines(1);
    }

    @Test
    public void removeTwoLinesWithMargin() {
        allowSize(80, 24);
        prepareShift(-2, 4, 21);
        allowMargineDefined(true);

        context.checking(new Expectations() {{
            oneOf(positionManager).currentPositionInScreen();
                will(returnValue(new Position(1, 5)));
            allowing(positionManager).getBottomMargin();
                will(returnValue(21));
        }});

        buffer.removeLines(2);
    }
}