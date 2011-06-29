package de.jowisoftware.ssh.client.tty;

import java.util.ArrayList;
import java.util.List;

import de.jowisoftware.ssh.client.ui.GfxChar;

public class ArrayListBuffer<T extends GfxChar> implements Buffer<T> {
    private final ArrayList<List<T>> chars = new ArrayList<List<T>>();
    private CursorPosition position = new CursorPosition(0, 0);

    @Override
    public int rows() {
        return chars.size();
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
        if (row >= chars.size()) {
            return null;
        } else if (column >= chars.get(row).size()) {
            return null;
        } else {
            return chars.get(row).get(column);
        }
    }

    @Override
    public int lengthOfLine(final int row) {
        return chars.get(row).size();
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
        while (chars.size() <= y) {
            chars.add(new ArrayList<T>());
        }
        while (chars.get(y).size() <= x) {
            chars.get(y).add(null);
        }
        chars.get(y).set(x, character);
    }

    @Override
    public void eraseDown() {
        while(chars.size() > position.getY()) {
            chars.remove(chars.size() - 1);
        }
    }
}
