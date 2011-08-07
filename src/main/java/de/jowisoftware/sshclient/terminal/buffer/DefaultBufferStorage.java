package de.jowisoftware.sshclient.terminal.buffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DefaultBufferStorage<T extends GfxChar> implements BufferStorage<T> {
    /**
     * displayed characters
     * 1. dimension = row, 2. dimension = row
     */
    private volatile GfxChar[][] lines;
    private T clearChar;

    public DefaultBufferStorage(final T clearChar, final int width, final int height) {
        this.clearChar = clearChar;
        newSize(width, height);
    }

    @Override
    public void setClearChar(final T clearChar) {
        this.clearChar = clearChar;
    }

    @Override
    public Position size() {
        return new Position(lines[0].length, lines.length);
    }

    @Override
    public void newSize(final int width, final int height) {
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
    }

    @Override
    public void shiftLines(final int offset, final int start, final int end) {
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
    @SuppressWarnings("unchecked")
    public T getCharacterAt(final int row, final int column) {
        return (T) lines[row][column];
    }

    @Override
    public void setCharacter(final int y, final int x, final T character) {
        lines[y][x] = character;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[][] cloneContent() {
        final T[][] content = (T[][]) new GfxChar[lines.length][lines[0].length];

        for (int i = 0; i < lines.length; ++i) {
            System.arraycopy(lines[i], 0, content[i], 0, lines[0].length);
        }

        return content;
    }

    @Override
    public void erase(final Range range) {
        if (range.topLeft.y == range.bottomRight.y) {
            for (int col = range.topLeft.x; col <= range.bottomRight.x; ++col) {
                lines[range.topLeft.y][col] = clearChar;
            }
        } else {
            for (int col = range.topLeft.x; col < lines[0].length; ++col) {
                lines[range.topLeft.y][col] = clearChar;
            }
            for (int row = range.topLeft.y + 1; row < range.bottomRight.y; ++row) {
                for (int col = 0; col < lines[row].length; ++col) {
                    lines[row][col] = clearChar;
                }
            }
            for (int col = 0; col <= range.bottomRight.x; ++col) {
                lines[range.bottomRight.y][col] = clearChar;
            }
        }
    }
}
