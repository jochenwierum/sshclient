package de.jowisoftware.ssh.client.terminal;

import java.util.ArrayList;
import java.util.List;

import de.jowisoftware.ssh.client.ui.GfxChar;

public class ArrayListBuffer<T extends GfxChar> implements Buffer<T> {
    private final ArrayList<ArrayList<T>> lines = new ArrayList<ArrayList<T>>();
    private CursorPosition position = new CursorPosition(0, 0);
    private final Renderer<T> renderer;

    public ArrayListBuffer(final Renderer<T> renderer) {
        this.renderer = renderer;
    }

    @Override
    public synchronized int rows() {
        return lines.size();
    }

    @Override
    public synchronized void setCursorPosition(final CursorPosition position) {
        this.position = position;
    }

    @Override
    public synchronized CursorPosition getCursorPosition() {
        return position;
    }

    @Override
    public synchronized T getCharacter(final int column, final int row) {
        if (row >= lines.size()) {
            return null;
        } else if (column >= lines.get(row).size()) {
            return null;
        } else {
            return lines.get(row).get(column);
        }
    }

    @Override
    public synchronized int lengthOfLine(final int row) {
        return lines.get(row).size();
    }

    @Override
    public synchronized void addNewLine() {
        position = new CursorPosition(0, position.getY() + 1);
    }

    @Override
    public synchronized void addCharacter(final T character) {
        setCharAt(position.getX(), position.getY(), character);
        position = position.offset(1, 0);
    }

    private synchronized void setCharAt(final int x, final int y, final T character) {
        while (lines.size() <= y) {
            lines.add(new ArrayList<T>());
        }
        while (lines.get(y).size() <= x) {
            lines.get(y).add(null);
        }
        lines.get(y).set(x, character);
    }

    @Override
    public synchronized void eraseToBottom() {
        eraseNextLineToBottom();
        eraseRestOfLine();
    }

    private synchronized void eraseNextLineToBottom() {
        while(lines.size() > position.getY() + 1) {
            lines.remove(lines.size() - 1);
        }
    }

    @Override
    public synchronized void eraseRestOfLine() {
        if (lines.size() > position.getY()) {
            final List<T> line = lines.get(position.getY());
            while(line.size() > position.getX()) {
                line.remove(line.size() - 1);
            }
        }
    }

    @Override
    public synchronized void eraseStartOfLine() {
        if (lines.size() > position.getY()) {
            final List<T> line = lines.get(position.getY());
            for (int i = 0; i <= position.getX(); ++i) {
                if (line.get(i) != null) {
                    line.set(i, null);
                }
            }
        }
    }

    @Override
    public synchronized void eraseFromTop() {
        eraseTopToPreviousLine();
        eraseStartOfLine();
    }

    private synchronized void eraseTopToPreviousLine() {
        for (int i = 0; i < Math.min(position.getY(), lines.size()); ++i) {
            lines.get(i).clear();
        }
    }

    @Override
    public synchronized void erase() {
        lines.clear();
    }

    @Override
    public synchronized void eraseLine() {
        if (lines.size() > position.getY()) {
            lines.get(position.getY()).clear();
        }
    }

    @Override
    public synchronized void render() {
        // TODO: cursor
        renderer.clear();
        final int startRow = Math.max(lines.size() - renderer.getLines(), 0);
        for (int row = startRow; row < lines.size(); ++row) {
            final int endCol = Math.min(renderer.getCharsPerLine(), lengthOfLine(row));

            for (int col = 0; col < endCol; ++col) {
                if (lines.get(row).get(col) != null) {
                    renderer.renderChar(lines.get(row).get(col), col, row);
                }
            }
        }
        renderer.swap();
    }

    // TODO: map terminal <-> buffer
    // TODO: setHeight, setWidth
}
