package de.jowisoftware.sshclient.terminal.buffer;

import de.jowisoftware.sshclient.terminal.gfx.GfxChar;

public class SynchronizedBuffer implements Buffer {
    private volatile FlippableBufferStorage storage;

    private final CursorPositionManager cursorPosition;
    private final TabStopManager tabstops;

    private boolean cursorIsRelativeToMargin = false;
    private boolean autoWrap = true;
    private boolean showCursor = true;

    private int width;
    private int height;

    public SynchronizedBuffer(
            final FlippableBufferStorage storage,
            final TabStopManager tabstopManager,
            final CursorPositionManager cursorPositionManager) {
        this.storage = storage;
        this.tabstops = tabstopManager;
        this.cursorPosition = cursorPositionManager;
    }

    public synchronized static SynchronizedBuffer createBuffer(
            final GfxChar initialClearChar,
            final int width, final int height,
            final int historySize, final TabStopManager tabstops) {
        final FlippableBufferStorage storage =
                SimpleFlippableBufferStorage.create(initialClearChar, width,
                        height, historySize);
        final CursorPositionManagerFeedback positionManagerCallback =
                createPositionManagerCallback(storage);
        final DefaultCursorPositionManager positionManager =
                new DefaultCursorPositionManager(positionManagerCallback,
                        width, height);

        final SynchronizedBuffer buffer = new SynchronizedBuffer(storage, tabstops,
                positionManager);
        buffer.width = width;
        buffer.height = height;
        return buffer;
    }

    private static CursorPositionManagerFeedback createPositionManagerCallback(
            final BufferStorage storage) {
        return new CursorPositionManagerFeedback() {
            @Override
            public synchronized void lineShiftingNeeded(final int offset, final int start, final int end) {
                storage.shiftLines(offset, start, end);
            }
        };
    }

    @Override
    public synchronized final void newSize(final int width, final int height) {
        storage.newSize(width, height);
        cursorPosition.newSize(width, height);

        this.width = width;
        this.height = height;
    }

    @Override
    public synchronized void setCursorPosition(final Position position) {
        if (cursorPosition.isMarginDefined() && cursorIsRelativeToMargin) {
            cursorPosition.setPositionSafelyInMargin(position);
        } else {
            cursorPosition.setPositionSafelyInScreen(position);
        }
    }

    @Override
    public synchronized Position getCursorPosition() {
        if (cursorPosition.isMarginDefined() && cursorIsRelativeToMargin) {
            return cursorPosition.currentPositionInMargin();
        } else {
            return cursorPosition.currentPositionInScreen();
        }
    }

    @Override
    public synchronized GfxChar getCharacter(final int row, final int column) {
        return storage.getCharacterAt(row - 1, column - 1);
    }

    @Override
    public synchronized void addCharacter(final GfxChar character) {
        wrapLineIfNeeded();
        final Position currentPosition = cursorPosition.currentPositionInScreen();

        cursorPosition.resetWouldWrap();
        storage.setCharacter(currentPosition.y - 1,
                currentPosition.x - 1, character);
        cursorPosition.moveToNextPosition(character.getCharCount());
    }

    private void wrapLineIfNeeded() {
        final Position currentPosition = cursorPosition.currentPositionInScreen();
        final boolean willWrapCursor = autoWrap && currentPosition.x == width
                && cursorPosition.wouldWrap();

        if (willWrapCursor) {
            final Position newPosition = currentPosition.withX(1).offset(0, 1);
            cursorPosition.setPositionSafelyInScreen(newPosition);
        }
    }

    @Override
    public synchronized SnapshotWithHistory createSnapshot() {
        final SnapshotWithHistory content = storage.cloneContentWithHistory();
        content.setCursorPosition(makeRenderCursor());
        return content;
    }

    private Position makeRenderCursor() {
        if (showCursor) {
            return cursorPosition.currentPositionInScreen();
        }
        return null;
    }

    @Override
    public synchronized void setMargin(final int rollRangeBegin, final int rollRangeEnd) {
        cursorPosition.setMargins(rollRangeBegin, rollRangeEnd);
        cursorPosition.setPositionSafelyInMargin(new Position(1, 1));
    }

    @Override
    public synchronized void resetMargin() {
        cursorPosition.setMargins(CursorPositionManager.NO_MARGIN_DEFINED,
                CursorPositionManager.NO_MARGIN_DEFINED);
    }

    @Override
    public synchronized void moveCursorUp() {
        cursorPosition.moveUpAndRoll();
    }

    @Override
    public synchronized void moveCursorDown(final boolean resetToFirstColumn) {
        cursorPosition.moveDownAndRoll();
        if (resetToFirstColumn) {
            cursorPosition.setPositionSafelyInScreen(
                    cursorPosition.currentPositionInScreen().withX(1));
        }
    }

    @Override
    public synchronized Position getSize() {
        return new Position(width, height);
    }

    @Override
    public synchronized void erase(final Range range) {
        storage.erase(range.offset(-1, -1));
    }

    @Override
    public synchronized void insertLines(final int linesCount) {
        if (cursorPosition.isMarginDefined()) {
            storage.shiftLines(linesCount,
                    cursorPosition.currentPositionInScreen().y - 1,
                    cursorPosition.getBottomMargin());
        } else {
            storage.shiftLines(linesCount,
                    cursorPosition.currentPositionInScreen().y - 1,
                    height);
        }
    }

    @Override
    public synchronized void setCursorRelativeToMargin(final boolean cursorIsRelativeToMargin) {
        this.cursorIsRelativeToMargin = cursorIsRelativeToMargin;
    }

    @Override
    public synchronized void tabulator(final TabulatorOrientation orientation) {
        final Position oldPosition = getCursorPosition();
        final Position newPosition;

        if (orientation == TabulatorOrientation.HORIZONTAL) {
            newPosition = tabstops.getNextHorizontalTabPosition(oldPosition);
        } else {
            newPosition = new Position(oldPosition.x, oldPosition.y + 1);
        }
        setCursorPosition(newPosition);
    }

    @Override
    public synchronized void setAutoWrap(final boolean autoWrap) {
        this.autoWrap = autoWrap;
    }

    @Override
    public synchronized void processBackspace() {
        final Position position = cursorPosition.currentPositionInScreen();
        if (autoWrap && position.x == 1) {
            cursorPosition.setPositionSafelyInScreen(
                    position.withX(width).offset(0, -1));
        } else {
            cursorPosition.setPositionSafelyInScreen(position.offset(-1, 0));
        }
    }

    @Override
    public synchronized void saveCursorPosition() {
        cursorPosition.save();
    }

    @Override
    public synchronized void restoreCursorPosition() {
        cursorPosition.restore();
    }

    @Override
    public synchronized void switchBuffer(final BufferSelection selection) {
        storage.flipTo(selection);
    }

    @Override
    public synchronized BufferSelection getSelectedBuffer() {
        return storage.getSelectedStorage();
    }

    @Override
    public synchronized void setShowCursor(final boolean doIt) {
        this.showCursor = doIt;
    }

    @Override
    public synchronized void setClearChar(final GfxChar clearChar) {
        storage.setClearChar(clearChar);
    }

    @Override
    public synchronized void shift(final int charCount) {
        final Position position = cursorPosition.currentPositionInScreen();
        storage.shiftColumns(charCount, position.x - 1, position.y - 1);
    }

    @Override
    public synchronized void removeLines(final int linesCount) {
        if (cursorPosition.isMarginDefined()) {
            storage.shiftLines(-linesCount,
                    cursorPosition.currentPositionInScreen().y - 1,
                    cursorPosition.getBottomMargin());
        } else {
            storage.shiftLines(-linesCount,
                    cursorPosition.currentPositionInScreen().y - 1,
                    height);
        }
    }

    @Override
    public int getHistorySize() {
        return storage.getHistorySize();
    }
}