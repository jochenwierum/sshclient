package de.jowisoftware.sshclient.terminal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.ui.GfxChar;

// TODO: is it possible to split this into two classes? e.g. Cursorposition + Array?
public class ArrayBuffer<T extends GfxChar> implements Buffer<T> {
    private static final Logger LOGGER = Logger.getLogger(ArrayBuffer.class);
    private static final int NO_ROLL_DEFINED = -1;

    /**
     * displayed characters
     * 1. dimension = row, 2. dimension = row
     */
    private volatile GfxChar[][] lines;
    private volatile Position position = new Position(1, 1);
    private final T clearChar;
    private int rollRangeBegin = NO_ROLL_DEFINED;
    private int rollRangeEnd = NO_ROLL_DEFINED;


    public ArrayBuffer(final T clearChar,
            final int width, final int height) {
        this.clearChar = clearChar;
        newSize(width, height);
    }

    @Override
    public void newSize(final int width, final int height) {
        synchronized(this) {
            final GfxChar[][] newLines = new GfxChar[height][width];
            for (int row = 0; row < newLines.length; ++row) {
                Arrays.fill(newLines[row], clearChar);
            }
            if (lines != null) {
                for (int i = 0; i < Math.min(lines.length, height); ++i) {
                    System.arraycopy(lines[i], 0, newLines[i], 0,
                            Math.min(lines[i].length, width));
                }
            }
            lines = newLines;
            setAndFixCursorPosition(position);
        }
    }


    private void setAndFixCursorPosition(final Position position) {
        int x = position.x;
        int y = position.y;

        if (x <= 0) {
            LOGGER.warn("Cursor was set to x = " + x + ", moving to 1");
            x = 1;
        } else if (position.x > lines[0].length) {
            LOGGER.debug("invalid terminal position, moving to next line: " + x + "/" + y);
            x = 1;
            ++y;
        }

        if (y <= 0) {
            LOGGER.warn("Cursor was set to y = " + y + ", movint to 1");
            y = 1;
        } else if (y > lines.length) {
            LOGGER.debug("invalid terminal position, shifting lines: " + x + "/" + y);
            shiftLines((-y - lines.length) % lines.length , 0, lines.length);
            y = lines.length;
        }
        this.position = new Position(x, y);
    }

    private void shiftLines(final int offset, final int start, final int end) {
        final List<GfxChar[]> newLines = new ArrayList<GfxChar[]>();
        for (final GfxChar[] line : lines) {
            newLines.add(line);
        }

        Collections.rotate(newLines, offset);

        final int clearStart;
        final int clearEnd;
        if (offset > 0) {
            clearStart = start;
            clearEnd = start + offset;
        } else {
            clearStart = end + offset;
            clearEnd = end;
        }

        for (int i = clearStart; i < clearEnd; ++i) {
            newLines.set(i, new GfxChar[lines[i].length]);
            Arrays.fill(newLines.get(i), clearChar);
        }

        for (int i = start; i < end; ++i) {
            lines[i] = newLines.get(i);
        }
    }

    @Override
    public void setCursorPosition(final Position position) {
        synchronized (this) {
            if (rollRangeBegin != NO_ROLL_DEFINED && rollRangeBegin != NO_ROLL_DEFINED) {
                setAndFixCursorPosition(position.offset(0, rollRangeBegin - 1));
            } else {
                setAndFixCursorPosition(position);
            }
        }
    }


    @Override
    public void setAbsoluteCursorPosition(final Position cursorPosition) {
        synchronized (this) {
            setAndFixCursorPosition(cursorPosition);
        }
    }

    @Override
    public void setSafeCursorPosition(final Position position) {
        synchronized (this) {
            this.position = position.moveInRange(new Range(getSize()));
        }
    }

    @Override
    public Position getCursorPosition() {
        synchronized (this) {
            if (rollRangeBegin != NO_ROLL_DEFINED && rollRangeEnd != NO_ROLL_DEFINED) {
                return position.offset(0, -rollRangeBegin + 1);
            } else {
                return position;
            }
        }
    }

    @Override
    public Position getAbsoluteCursorPosition() {
        return position;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getCharacter(final int row, final int column) {
        synchronized(this) {
            return (T) lines[row - 1][column - 1];
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
            lines[position.y - 1][position.x - 1] = character;
            position = position.offset(1, 0).moveInRange(new Range(getSize()));
        }
    }

    @Override
    public void render(final Renderer<T> renderer) {
        final T[][] content = cloneContent();

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

    @SuppressWarnings("unchecked")
    private T[][] cloneContent() {
        synchronized(this) {
            final T[][] content = (T[][]) new GfxChar[lines.length][lines[0].length];

            for (int i = 0; i < lines.length; ++i) {
                System.arraycopy(lines[i], 0, content[i], 0, lines[0].length);
            }

            return content;
        }
    }

    @Override
    public void setRollRange(final int rollRangeBegin, final int rollRangeEnd) {
        this.rollRangeBegin = rollRangeBegin;
        this.rollRangeEnd = rollRangeEnd;
    }

    @Override
    public void deleteRollRange() {
        rollRangeBegin = NO_ROLL_DEFINED;
        rollRangeEnd = NO_ROLL_DEFINED;
    }

    @Override
    public void moveCursorUpAndRoll() {
        synchronized(this) {
            int y = position.y;
            if (rollRangeBegin == NO_ROLL_DEFINED) {
                setSafeCursorPosition(new Position(1, y - 1));
            } else {
                if (y == rollRangeBegin) {
                    shiftLines(1, rollRangeBegin - 1, rollRangeEnd);
                } else {
                    --y;
                }
                setAndFixCursorPosition(new Position(1, y));
            }
        }
    }

    @Override
    public void moveCursorDownAndRoll(final boolean resetToFirstColumn) {
        synchronized(this) {
            int y = position.y;
            final int x = resetToFirstColumn ? 1 : position.x;
            if (rollRangeEnd == NO_ROLL_DEFINED) {
                setCursorPosition(new Position(x, y + 1));
            } else {
                if (y == rollRangeEnd) {
                    shiftLines(-1, rollRangeBegin - 1, rollRangeEnd);
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
            return new Position(lines[0].length, lines.length);
        }
    }

    @Override
    public void erase(final Range range) {
        synchronized(this) {
            if (range.topLeft.y == range.bottomRight.y) {
                for (int col = range.topLeft.x - 1; col < range.bottomRight.x; ++col) {
                    lines[range.topLeft.y - 1][col] = clearChar;
                }
            } else {
                for (int col = range.topLeft.x - 1; col < lines[0].length; ++col) {
                    lines[range.topLeft.y - 1][col] = clearChar;
                }
                for (int row = range.topLeft.y; row < range.bottomRight.y - 1; ++row) {
                    for (int col = 0; col < lines[row].length; ++col) {
                        lines[row][col] = clearChar;
                    }
                }
                for (int col = 0; col < range.bottomRight.x; ++col) {
                    lines[range.bottomRight.y - 1][col] = clearChar;
                }
            }
        }
    }
}