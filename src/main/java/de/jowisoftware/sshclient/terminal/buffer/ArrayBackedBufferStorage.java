package de.jowisoftware.sshclient.terminal.buffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.jowisoftware.sshclient.terminal.gfx.GfxChar;
import de.jowisoftware.sshclient.util.RingBuffer;

public class ArrayBackedBufferStorage implements BufferStorage {
    /**
     * displayed characters
     * 1. dimension = row, 2. dimension = row
     */
    private volatile GfxChar[][] lines;
    private final GfxChar backgroundChar;
    private GfxChar clearChar;
    private final RingBuffer<GfxChar[]> history;

    public ArrayBackedBufferStorage(final GfxChar backgroundChar,
            final int width, final int height, final RingBuffer<GfxChar[]> history) {
        this.backgroundChar = backgroundChar;
        this.clearChar = backgroundChar;
        this.history = history;
        newSize(width, height);
    }

    @Override
    public void setClearChar(final GfxChar clearChar) {
        this.clearChar = clearChar;
    }

    @Override
    public void newSize(final int width, final int height) {
        final GfxChar[][] newLines = new GfxChar[height][width];
        for (GfxChar[] line : newLines) {
            Arrays.fill(line, clearChar);
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
        if (start == 0 && offset < 0) {
            copyToHistory(history, -offset);
        }

        final List<GfxChar[]> newLines = new ArrayList<>();
        Collections.addAll(newLines, lines);
        Collections.rotate(newLines, offset);

        final int clearStart;
        final int clearEnd;
        if (offset > 0) {
            clearStart = start;
            clearEnd = Math.min(start + offset, lines.length);
        } else {
            clearStart = Math.max(0, end + offset);
            clearEnd = end;
        }

        for (int i = clearStart; i < clearEnd; ++i) {
            newLines.set(i, new GfxChar[lines[i].length]);
            Arrays.fill(newLines.get(i), backgroundChar);
        }

        for (int i = start; i < end; ++i) {
            lines[i] = newLines.get(i);
        }
    }

    @Override
    public GfxChar getCharacterAt(final int row, final int column) {
        return lines[row][column];
    }

    @Override
    public void setCharacter(final int y, final int x, final GfxChar character) {
        lines[y][x] = character;
        for (int offset = 1; offset < character.getCharCount(); ++offset) {
            lines[y][x + offset] = EMPTY;
        }
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

    @Override
    public void shiftColumns(final int offset, final int x, final int y) {
        final int width = lines[0].length;
        if (offset > 0) {
            shiftColumnWithPositiveOffset(offset, x, y, width);
        } else {
            shiftColumnWithNegativeOffset(offset, x, y, width);
        }
    }

    private void shiftColumnWithNegativeOffset(final int offset, final int x,
            final int y, final int width) {
        for (int i = width - 1; i >= x; --i) {
            if (i + offset < x) {
                lines[y][i] = clearChar;
            } else {
                lines[y][i] = lines[y][i + offset];
            }
        }
    }

    private void shiftColumnWithPositiveOffset(final int offset, final int x,
            final int y, final int width) {
        for (int i = x; i < width; ++i) {
            if (i + offset < width) {
                lines[y][i] = lines[y][i + offset];
            } else {
                lines[y][i] = clearChar;
            }
        }
    }

    private void copyToHistory(final RingBuffer<GfxChar[]> history, final int count) {
        for (int i = 0; i < count; ++i) {
            history.append(lines[i]);
        }
    }

    @Override
    public SnapshotWithHistory cloneContentWithHistory() {
        final GfxChar[][] content = new GfxChar[lines.length][lines[0].length];

        for (int i = 0; i < lines.length; ++i) {
            System.arraycopy(lines[i], 0, content[i], 0, lines[0].length);
        }

        return new ArrayBackedSnapshotWithHistory(content, history.getSnapshot());
    }

    @Override
    public int getHistorySize() {
        return history.size();
    }
}
