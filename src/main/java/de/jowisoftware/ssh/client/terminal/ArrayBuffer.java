package de.jowisoftware.ssh.client.terminal;

import java.util.Arrays;

import de.jowisoftware.ssh.client.ui.GfxChar;

public class ArrayBuffer<T extends GfxChar> implements Buffer<T> {
    /**
     * displayed characters
     * 1. dimension = row, 2. dimension = row
     */
    private GfxChar[][] lines;
    private CursorPosition position = new CursorPosition(1, 1);
    private final Renderer<T> renderer;
    private final T clearChar;


    public ArrayBuffer(final Renderer<T> renderer, final T clearChar,
            final int width, final int height) {
        this.renderer = renderer;
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
                    for (int j = 0; j < Math.min(lines[i].length, width); ++j) {
                        newLines[i][j] = lines[i][j];
                    }
                }
            }
            lines = newLines;
        }
    }

    @Override
    public void setCursorPosition(final CursorPosition position) {
        synchronized (this) {
            this.position = position;
        }
    }

    @Override
    public CursorPosition getCursorPosition() {
        return position;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getCharacter(final int row, final int column) {
        return (T) lines[row - 1][column - 1];
    }

    @Override
    public void addNewLine() {
        position = new CursorPosition(1, position.getY() + 1);
    }

    @Override
    public void addCharacter(final T character) {
        synchronized(this) {
            // TODO: check position
            lines[position.getY() - 1][position.getX() - 1] = character;
            position = position.offset(1, 0);
            if (position.getX() == lines[0].length + 1) {
                position = new CursorPosition(1, position.getY() + 1);
            }
        }
    }

    @Override
    public void eraseToBottom() {
        synchronized(this) {
            eraseNextLineToBottomUnsynced();
            eraseRestOfLineUnsynced();
        }
    }

    private void eraseNextLineToBottomUnsynced() {
        for (int row = position.getY(); row < lines.length; ++row) {
            for (int col = 0; col < lines[0].length; ++col) {
                lines[row][col] = clearChar;
            }
        }
    }

    private void eraseRestOfLineUnsynced() {
        for (int col = position.getX() - 1; col < lines[position.getY() - 1].length; ++col) {
            lines[position.getY() - 1][col] = clearChar;
        }
    }

    private void eraseStartOfLineUnsynced() {
        for (int col = 0; col <= position.getX() - 1; ++col) {
            lines[position.getY() - 1][col] = clearChar;
        }
    }

    @Override
    public void eraseRestOfLine() {
        synchronized(this) {
            eraseRestOfLineUnsynced();
        }
    }

    @Override
    public void eraseStartOfLine() {
        synchronized(this) {
            eraseStartOfLineUnsynced();
        }
    }

    @Override
    public void eraseFromTop() {
        synchronized(this) {
            eraseStartOfLineUnsynced();
            eraseTopToPreviousLineUnsynced();
        }
    }

    private void eraseTopToPreviousLineUnsynced() {
        for (int row = 0; row < position.getY() - 1; ++row) {
            for (int col = 0; col < lines[row].length; ++col) {
                lines[row][col] = clearChar;
            }
        }
    }

    @Override
    public void erase() {
        synchronized(this) {
            for (int row = 0; row < lines.length; ++row) {
                for (int col = 0; col < lines[row].length; ++col) {
                    lines[row][col] = clearChar;
                }
            }
        }
    }

    @Override
    public void eraseLine() {
        synchronized(this) {
            for (int col = 0; col < lines[0].length; ++col) {
                lines[position.getY() - 1][col] = clearChar;
            }
        }
    }

    @Override
    public void render() {
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
        return col == position.getX() && row == position.getY();
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
}
