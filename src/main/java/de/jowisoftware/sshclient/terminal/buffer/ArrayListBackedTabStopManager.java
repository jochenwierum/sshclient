package de.jowisoftware.sshclient.terminal.buffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrayListBackedTabStopManager implements TabStopManager {
    private final List<Integer> tabstopList = new ArrayList<Integer>();
    private int width = 0;

    public ArrayListBackedTabStopManager(final int width) {
        newWidth(width);
    }

    @Override
    public Position getNextHorizontalTabPosition(final Position position) {
        final int newY = position.y;

        final int pos = nextXPosition(position);
        if (pos >= tabstopList.size()) {
            return new Position(width, newY);
        } else {
            return new Position(tabstopList.get(pos), newY);
        }
    }

    private int nextXPosition(final Position position) {
        final int index = Collections.binarySearch(tabstopList, position.x);
        if (index < 0) {
            return -index - 1;
        } else {
            return index + 1;
        }
    }

    @Override
    public void newWidth(final int newWidth) {
        removeItemsBiggerThan(newWidth);
        fillNewTabs(width, newWidth);
        this.width = newWidth;
    }

    private void fillNewTabs(final int from, final int to) {
        final int newStart = (from / 8) * 8 + 1;
        for (int i = newStart; i < to; i += 8) {
            tabstopList.add(i);
        }
    }

    private void removeItemsBiggerThan(final int newWidth) {
        int size = tabstopList.size();
        while (size > 0 && tabstopList.get(size - 1) >= newWidth) {
            tabstopList.remove(--size);
        }
    }

    @Override
    public void removeAll() {
        tabstopList.clear();
    }

    @Override
    public void addTab(final int column) {
        tabstopList.add(column);
    }

    @Override
    public void removeTab(final int i) {
        tabstopList.remove((Object) i);
    }
}
