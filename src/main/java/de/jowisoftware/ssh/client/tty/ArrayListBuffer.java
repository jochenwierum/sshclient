package de.jowisoftware.ssh.client.tty;

import java.util.ArrayList;
import java.util.List;

import de.jowisoftware.ssh.client.ui.GfxChar;

public class ArrayListBuffer<T extends GfxChar> implements Buffer<T> {
    private final ArrayList<ArrayList<T>> lines = new ArrayList<ArrayList<T>>();
    private CursorPosition position = new CursorPosition(0, 0);

    @Override
    public int rows() {
        return lines.size();
    }

    @Override
    public void setCursorPosition(final CursorPosition position) {
        this.position = position;
    }

    @Override
    public CursorPosition getCursorPosition() {
        return position;
    }

    @Override
    public T getCharacter(final int column, final int row) {
        if (row >= lines.size()) {
            return null;
        } else if (column >= lines.get(row).size()) {
            return null;
        } else {
            return lines.get(row).get(column);
        }
    }

    @Override
    public int lengthOfLine(final int row) {
        return lines.get(row).size();
    }

    @Override
    public void addNewLine() {
        position = new CursorPosition(0, position.getY() + 1);
    }

    @Override
    public void addCharacter(final T character) {
        setCharAt(position.getX(), position.getY(), character);
        position = position.offset(1, 0);
    }

    private void setCharAt(final int x, final int y, final T character) {
        while (lines.size() <= y) {
            lines.add(new ArrayList<T>());
        }
        while (lines.get(y).size() <= x) {
            lines.get(y).add(null);
        }
        lines.get(y).set(x, character);
    }

    @Override
    public void eraseToBottom() {
        eraseNextLineToBottom();
        eraseRestOfLine();
    }

    private void eraseNextLineToBottom() {
        while(lines.size() > position.getY() + 1) {
            lines.remove(lines.size() - 1);
        }
    }

    @Override
    public void eraseRestOfLine() {
        if (lines.size() > position.getY()) {
            final List<T> line = lines.get(position.getY());
            while(line.size() > position.getX()) {
                line.remove(line.size() - 1);
            }
        }
    }

    @Override
    public void eraseStartOfLine() {
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
    public void eraseFromTop() {
        eraseTopToPreviousLine();
        eraseStartOfLine();
    }

    private void eraseTopToPreviousLine() {
        for (int i = 0; i < Math.min(position.getY(), lines.size()); ++i) {
            lines.get(i).clear();
        }
    }

    @Override
    public void erase() {
        lines.clear();
    }

    @Override
    public void eraseLine() {
        if (lines.size() > position.getY()) {
            lines.get(position.getY()).clear();
        }
    }
}
