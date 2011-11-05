package de.jowisoftware.sshclient.terminal.buffer;

import java.util.HashSet;
import java.util.Set;

public class SynchronizedBuffer implements Buffer {
    private volatile FlippableBufferStorage storage;

    private final CursorPositionManager cursorPosition;
    private final TabStopManager tabstops;

    private boolean cursorIsRelativeToMargin = false;
    private boolean autoWrap = true;
    private boolean showCursor = true;

    public SynchronizedBuffer(
            final FlippableBufferStorage storage,
            final TabStopManager tabstopManager,
            final CursorPositionManager cursorPositionManager) {
        this.storage = storage;
        this.tabstops = tabstopManager;
        this.cursorPosition = cursorPositionManager;
    }

    public static SynchronizedBuffer createBuffer(
            final GfxChar initialClearChar,
            final int width, final int height, final TabStopManager tabstops) {
        final FlippableBufferStorage storage =
                SimpleFlippableBufferStorage.create(initialClearChar, width, height);
        final CursorPositionManagerFeedback positionManagerCallback =
                createPositionManagerCallback(storage);
        final DefaultCursorPositionManager positionManager =
                new DefaultCursorPositionManager(positionManagerCallback,
                        width, height);

        return new SynchronizedBuffer(storage, tabstops,
                positionManager);
    }

    private static CursorPositionManagerFeedback createPositionManagerCallback(
            final BufferStorage storage) {
        return new CursorPositionManagerFeedback() {
            @Override
            public void lineShiftingNeeded(final int offset, final int start, final int end) {
                storage.shiftLines(offset, start, end);
            }
        };
    }

    @Override
    public final void newSize(final int width, final int height) {
        synchronized(this) {
            storage.newSize(width, height);
            cursorPosition.newSize(width, height);
        }
    }

    @Override
    public void setCursorPosition(final Position position) {
        synchronized (this) {
            if (cursorPosition.isMarginDefined() && cursorIsRelativeToMargin) {
                cursorPosition.setPositionSafelyInMargin(position);
            } else {
                cursorPosition.setPositionSafelyInScreen(position);
            }
        }
    }

    @Override
    public Position getCursorPosition() {
        synchronized (this) {
            if (cursorPosition.isMarginDefined() && cursorIsRelativeToMargin) {
                return cursorPosition.currentPositionInMargin();
            } else {
                return cursorPosition.currentPositionInScreen();
            }
        }
    }

    @Override
    public GfxChar getCharacter(final int row, final int column) {
        synchronized(this) {
            return storage.getCharacterAt(row - 1, column - 1);
        }
    }

    @Override
    public void addCharacter(final GfxChar character) {
        synchronized(this) {
            wrapLineIfNeeded();
            final Position currentPosition = cursorPosition.currentPositionInScreen();

            cursorPosition.resetWouldWrap();
            storage.setCharacter(currentPosition.y - 1,
                    currentPosition.x - 1, character);
            cursorPosition.moveToNextPosition();
        }
    }

    private void wrapLineIfNeeded() {
        final Position currentPosition = cursorPosition.currentPositionInScreen();
        final boolean willWrapCursor = autoWrap && currentPosition.x == getSize().x
                && cursorPosition.wouldWrap();

        if (willWrapCursor) {
            final Position newPosition = currentPosition.withX(1).offset(0, 1);
            cursorPosition.setPositionSafelyInScreen(newPosition);
        }
    }

    @Override
    public void render(final Renderer renderer) {
        final GfxChar[][] content;
        synchronized(this) {
            content = storage.cloneContent();
        }

        synchronized(renderer) {
            renderer.clear();
            for (int row = 0; row < content.length; ++row) {
                for (int col = 0; col < content[0].length; ++col) {
                    renderer.renderChar(content[row][col], col, row,
                            makeRenderFlags(row, col));
                }
            }
            renderer.swap();
        }
    }

    private Set<RenderFlag> makeRenderFlags(final int row, final int col) {
        final Set<RenderFlag> flags = new HashSet<RenderFlag>();
        if (showCursor && isNullBasedCursorAt(col, row)) {
            flags.add(RenderFlag.CURSOR);
        }
        return flags;
    }

    private boolean isNullBasedCursorAt(final int col, final int row) {
        return cursorPosition.isAt(col - 1, row - 1);
    }

    @Override
    public void setMargin(final int rollRangeBegin, final int rollRangeEnd) {
        cursorPosition.setMargins(rollRangeBegin, rollRangeEnd);
        cursorPosition.setPositionSafelyInMargin(new Position(1, 1));
    }

    @Override
    public void resetMargin() {
        cursorPosition.setMargins(CursorPositionManager.NO_MARGIN_DEFINED,
                CursorPositionManager.NO_MARGIN_DEFINED);
    }

    @Override
    public void moveCursor() {
        synchronized(this) {
            cursorPosition.moveUpAndRoll();
        }
    }

    @Override
    public void moveCursorDown(final boolean resetToFirstColumn) {
        synchronized(this) {
            cursorPosition.moveDownAndRoll();
            if (resetToFirstColumn) {
                cursorPosition.setPositionSafelyInScreen(
                        cursorPosition.currentPositionInScreen().withX(1));
            }
        }
    }

    @Override
    public Position getSize() {
        synchronized(this) {
            return storage.size();
        }
    }

    @Override
    public void erase(final Range range) {
        synchronized(this) {
            storage.erase(range.offset(-1, -1));
        }
    }

    @Override
    public void insertLines(final int linesCount) {
        synchronized(this) {
            if (cursorPosition.isMarginDefined()) {
                storage.shiftLines(linesCount,
                        cursorPosition.currentPositionInScreen().y - 1,
                        cursorPosition.getBottomMargin());
            } else {
                storage.shiftLines(linesCount,
                        cursorPosition.currentPositionInScreen().y - 1,
                        storage.size().y);
            }
        }
    }

    @Override
    public void setCursorRelativeToMargin(final boolean cursorIsRelativeToMargin) {
        this.cursorIsRelativeToMargin = cursorIsRelativeToMargin;
    }

    @Override
    public void tabulator(final TabulatorOrientation orientation) {
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
    public void setAutoWrap(final boolean autoWrap) {
        this.autoWrap = autoWrap;
    }

    @Override
    public void processBackspace() {
        final Position position = cursorPosition.currentPositionInScreen();
        if (autoWrap && position.x == 1) {
            cursorPosition.setPositionSafelyInScreen(
                    position.withX(getSize().x).offset(0, -1));
        } else {
            cursorPosition.setPositionSafelyInScreen(position.offset(-1, 0));
        }
    }

    @Override
    public void saveCursorPosition() {
        cursorPosition.save();
    }

    @Override
    public void restoreCursorPosition() {
        cursorPosition.restore();
    }

    @Override
    public void switchBuffer(final BufferSelection selection) {
        storage.flipTo(selection);
    }

    @Override
    public BufferSelection getSelectedBuffer() {
        return storage.getSelectedStorage();
    }

    @Override
    public void setShowCursor(final boolean doIt) {
        this.showCursor = doIt;
    }

    @Override
    public void setClearChar(final GfxChar clearChar) {
        storage.setClearChar(clearChar);
    }

    @Override
    public void shift(final int charCount) {
        final Position position = cursorPosition.currentPositionInScreen();
        storage.shiftColumns(charCount, position.x - 1, position.y - 1);
    }
}