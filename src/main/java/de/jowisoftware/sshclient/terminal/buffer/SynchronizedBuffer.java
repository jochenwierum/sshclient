package de.jowisoftware.sshclient.terminal.buffer;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

// TODO: extract CursorPosition
public class SynchronizedBuffer implements Buffer {
    private static final Logger LOGGER = Logger.getLogger(SynchronizedBuffer.class);
    private static final int NO_MARGIN_DEFINED = -1;

    private volatile BufferStorage storage;
    private volatile BufferStorage defaultStorage;
    private volatile BufferStorage alternativeStorage;
    private volatile Position position = new Position(1, 1);

    private final TabStopManager tabstops;

    private int topMargin = NO_MARGIN_DEFINED;
    private int bottomMargin = NO_MARGIN_DEFINED;
    private boolean cursorIsRelativeToMargin = false;
    private boolean autoWrap = true;
    private boolean wouldWrap;
    private Position savedCursorPosition;
    private boolean showCursor = true;

    public SynchronizedBuffer(final GfxChar clearChar,
            final int width, final int height) {
        this(new SynchronizedArrayBackedBufferStorage(clearChar, width, height),
                new SynchronizedArrayBackedBufferStorage(clearChar, width, height),
                new ArrayListBackedTabStopManager(width));
    }

    public SynchronizedBuffer(final BufferStorage storage,
            final BufferStorage alternativeStorage,
            final TabStopManager tabstopManager) {
        this.storage = storage;
        this.defaultStorage = storage;
        this.alternativeStorage = alternativeStorage;
        this.tabstops = tabstopManager;
    }

    @Override
    public final void newSize(final int width, final int height) {
        synchronized(this) {
            defaultStorage.newSize(width, height);
            alternativeStorage.newSize(width, height);
            setAndFixCursorPosition(position);
            tabstops.newWidth(width);
        }
    }

    private void setAndFixCursorPosition(final Position newPosition) {
        final Position size = storage.size();
        if (newPosition.y > size.y) {
            LOGGER.debug("invalid terminal position, shifting lines: " +
                    newPosition.x + "/" + newPosition.y);
            storage.shiftLines((-newPosition.y - size.y) % size.y,
                    0, size.y);
        }
        this.position = newPosition.moveInRange(size.toRange());
        wouldWrap = false;
    }

    @Override
    public void setCursorPosition(final Position position) {
        synchronized (this) {
            final boolean isMarginDefined =
                    topMargin != NO_MARGIN_DEFINED && bottomMargin != NO_MARGIN_DEFINED;
            if (isMarginDefined && cursorIsRelativeToMargin) {
                setAndFixCursorPosition(position.offset(0, topMargin - 1));
            } else {
                setAndFixCursorPosition(position);
            }
        }
    }

    @Override
    public Position getCursorPosition() {
        synchronized (this) {
            final boolean isMarginDefined =
                    topMargin != NO_MARGIN_DEFINED && bottomMargin != NO_MARGIN_DEFINED;
            if (isMarginDefined && cursorIsRelativeToMargin) {
                return position.offset(0, -topMargin + 1);
            } else {
                return position;
            }
        }
    }

    public Position getAbsoluteCursorPosition() {
        return position;
    }

    @Override
    public GfxChar getCharacter(final int row, final int column) {
        synchronized(this) {
            return storage.getCharacterAt(row - 1, column - 1);
        }
    }

    @Override
    public void addNewLine() {
        synchronized(this) {
            moveCursorDownAndRoll(true);
        }
    }

    @Override
    public void addCharacter(final GfxChar character) {
        synchronized(this) {
            if (position.x == getSize().x && wouldWrap && autoWrap) {
                setAndFixCursorPosition(position.offset(0, 1).withX(0));
            }
            wouldWrap = false;
            storage.setCharacter(position.y - 1, position.x - 1, character);
            moveCursorToNextPosition();
        }
    }

    private void moveCursorToNextPosition() {
        position = position.offset(1, 0);
        wouldWrap = (position.x == getSize().x + 1);
        position = position.moveInRange(getSize().toRange());
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
                            makeRenderFlags(row, col, content[0].length));
                }
            }
            renderer.swap();
        }
    }

    private Set<RenderFlag> makeRenderFlags(final int row, final int col,
            final int length) {
        final Set<RenderFlag> flags = new HashSet<RenderFlag>();
        if (showCursor && isCursorAt(col, row, length)) {
            flags.add(RenderFlag.CURSOR);
        }
        return flags;
    }

    private boolean isCursorAt(final int col, final int row, final int length) {
        return col == position.x - 1 && row == position.y - 1;
    }

    @Override
    public void setMargin(final int rollRangeBegin, final int rollRangeEnd) {
        this.topMargin = rollRangeBegin;
        this.bottomMargin = rollRangeEnd;
        setCursorPosition(new Position(1, 1));
    }

    @Override
    public void resetMargin() {
        topMargin = NO_MARGIN_DEFINED;
        bottomMargin = NO_MARGIN_DEFINED;
    }

    @Override
    public void moveCursorUpAndRoll() {
        synchronized(this) {
            if (topMargin == NO_MARGIN_DEFINED) {
                this.position = position.offset(0, -1).moveInRange(position.toRange());
            } else {
                if (position.y == topMargin) {
                    storage.shiftLines(1, topMargin - 1, bottomMargin);
                } else {
                    this.position = position.offset(0, -1);
                }
            }
        }
    }

    @Override
    public void moveCursorDownAndRoll(final boolean resetToFirstColumn) {
        synchronized(this) {
            int y = position.y;
            final int x = resetToFirstColumn ? 1 : position.x;
            if (bottomMargin == NO_MARGIN_DEFINED) {
                setCursorPosition(new Position(x, y + 1));
            } else {
                if (y == bottomMargin) {
                    storage.shiftLines(-1, topMargin - 1, bottomMargin);
                } else {
                    ++y;
                }
                setAndFixCursorPosition(new Position(x, y));
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
            if (bottomMargin != NO_MARGIN_DEFINED) {
                storage.shiftLines(linesCount, position.y - 1, bottomMargin);
            } else {
                storage.shiftLines(linesCount, position.y - 1, storage.size().y);
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
            // TODO: implement this
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
        if (autoWrap && position.x == 1) {
            setAndFixCursorPosition(position.offset(getSize().x, -1));
        } else {
            setAndFixCursorPosition(position.offset(-1, 0));
        }
    }

    @Override
    public void saveCursorPosition() {
        savedCursorPosition = position;
    }

    @Override
    public void restoreCursorPosition() {
        setAndFixCursorPosition(savedCursorPosition);
    }

    @Override
    public void switchBuffer(final BufferSelection selection) {
        if (selection.equals(BufferSelection.PRIMARY)) {
            storage = defaultStorage;
        } else {
            storage = alternativeStorage;
        }
    }

    @Override
    public BufferSelection getSelectedBuffer() {
        if (storage == defaultStorage) {
            return BufferSelection.PRIMARY;
        } else {
            return BufferSelection.ALTERNATIVE;
        }
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
        storage.shiftColumns(charCount, position.x - 1, position.y - 1);
    }
}