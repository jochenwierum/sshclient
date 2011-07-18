package de.jowisoftware.sshclient.terminal.buffer;

import org.apache.log4j.Logger;


public class DefaultBuffer<T extends GfxChar> implements Buffer<T> {
    private static final Logger LOGGER = Logger.getLogger(DefaultBuffer.class);
    private static final int NO_MARGIN_DEFINED = -1;

    private volatile BufferStorage<T> storage;
    private volatile Position position = new Position(1, 1);

    private int topMargin = NO_MARGIN_DEFINED;
    private int bottomMargin = NO_MARGIN_DEFINED;
    private boolean cursorIsRelativeToMargin = false;


    public DefaultBuffer(final T clearChar,
            final int width, final int height) {
        storage = new DefaultBufferStorage<T>(clearChar, width, height);
    }

    public DefaultBuffer(final BufferStorage<T> storage) {
        this.storage = storage;
    }

    @Override
    public void newSize(final int width, final int height) {
        synchronized(this) {
            storage.newSize(width, height);
            setAndFixCursorPosition(position);
        }
    }

    private void setAndFixCursorPosition(final Position position) {
        if (position.y > storage.height()) {
            LOGGER.debug("invalid terminal position, shifting lines: " +
                    position.x + "/" + position.y);
            storage.shiftLines((-position.y - storage.height()) % storage.height(),
                    0, storage.height());
        }
        this.position = position.moveInRange(getSize().toRange());
    }

    @Override
    public void setCursorPosition(final Position position) {
        synchronized (this) {
            final boolean isMarginDefined =
                    topMargin != NO_MARGIN_DEFINED && topMargin != NO_MARGIN_DEFINED;
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
    public T getCharacter(final int row, final int column) {
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
    public void addCharacter(final T character) {
        synchronized(this) {
            storage.setCharacter(position.y - 1, position.x - 1, character);
            position = position.offset(1, 0).moveInRange(getSize().toRange());
        }
    }

    @Override
    public void render(final Renderer<T> renderer) {
        final T[][] content;
        synchronized(this) {
            content = storage.cloneContent();
        }

        synchronized(renderer) {
            renderer.clear();
            for (int row = 0; row < content.length; ++row) {
                for (int col = 0; col < content[0].length; ++col) {
                    renderer.renderChar(content[row][col], col, row,
                            isCursorAt(col, row));
                }
            }
            renderer.swap();
        }
    }

    private boolean isCursorAt(final int col, final int row) {
        return col == position.x - 1 && row == position.y - 1;
    }

    @Override
    public void setMargin(final int rollRangeBegin, final int rollRangeEnd) {
        this.topMargin = rollRangeBegin;
        this.bottomMargin = rollRangeEnd;
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
                final int y = position.y;
                if (y == topMargin) {
                    storage.shiftLines(1, topMargin - 1, bottomMargin);
                } else {
                    this.position = position.withY(y - 1);
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
            return new Position(storage.width(), storage.height());
        }
    }

    @Override
    public void erase(final Range range) {
        synchronized(this) {
            storage.erase(range);
        }
    }

    @Override
    public void insertLines(final int linesCount) {
        synchronized(this) {
            if (bottomMargin != NO_MARGIN_DEFINED) {
                storage.shiftLines(linesCount, position.y - 1, bottomMargin);
            } else {
                storage.shiftLines(linesCount, position.y - 1, storage.height());
            }
        }
    }

    @Override
    public void setCursorRelativeToMargin(final boolean b) {
        cursorIsRelativeToMargin  = b;
    }

    @Override
    public void tapstop(final Tabstop vertical) {
        // TODO Auto-generated method stub
    }
}