package de.jowisoftware.sshclient.terminal.buffer;


public interface TabStopManager {
    Position getNextHorizontalTabPosition(final Position position);

    void newWidth(final int newWidth);

    void addTab(final int column);

    void removeAll();
    void removeTab(final int i);
}