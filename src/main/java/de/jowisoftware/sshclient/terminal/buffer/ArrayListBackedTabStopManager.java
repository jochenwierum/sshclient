package de.jowisoftware.sshclient.terminal.buffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

public class ArrayListBackedTabStopManager implements TabStopManager {
    private static final Logger LOGGER = Logger
            .getLogger(ArrayListBackedTabStopManager.class);

    private final List<Integer> tabstopList = new ArrayList<>();
    private int width = 0;

    public ArrayListBackedTabStopManager(final int width) {
        newWidth(width);
    }

    @Override
    public Position getNextHorizontalTabPosition(final Position position) {
        final int newY = position.y;

        final int pos = nextXPosition(position);
        final Position newPos;
        if (pos >= tabstopList.size()) {
            newPos = new Position(width, newY);
        } else {
            newPos = new Position(tabstopList.get(pos), newY);
        }

        LOGGER.debug("Next tab position for " + position + " is " + newPos);
        return newPos;
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
            addTab(i);
        }
    }

    private void removeItemsBiggerThan(final int newWidth) {
        int size = tabstopList.size();
        while (size > 0 && tabstopList.get(size - 1) >= newWidth) {
            removeTab(tabstopList.get(--size));
        }
    }

    @Override
    public void removeAll() {
        LOGGER.debug("Removing all tabs");
        tabstopList.clear();
    }

    @Override
    public void addTab(final int column) {
        if (!tabstopList.contains(column)) {
            LOGGER.debug("Adding new tab position at column " + column);
            tabstopList.add(column);
            Collections.sort(tabstopList);
        }
    }

    @Override
    public void removeTab(final int i) {
        LOGGER.debug("Removing tab position at column " + i);
        tabstopList.remove(Integer.valueOf(i));
    }
}
