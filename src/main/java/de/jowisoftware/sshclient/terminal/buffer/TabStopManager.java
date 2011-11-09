package de.jowisoftware.sshclient.terminal.buffer;

import java.io.Serializable;

public interface TabStopManager extends Serializable {
    Position getNextHorizontalTabPosition(final Position position);

    void newWidth(final int newWidth);

    void addTab(final int column);

    void removeAll();
    void removeTab(final int i);
}