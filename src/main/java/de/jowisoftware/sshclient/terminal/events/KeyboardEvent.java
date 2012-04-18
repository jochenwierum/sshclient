package de.jowisoftware.sshclient.terminal.events;

import java.awt.event.KeyListener;

// TODO: can these interfaces be separated?
public interface KeyboardEvent extends KeyListener {
    void newCursorKeysIsAppMode(final boolean value);
    void newNumblockAppMode(boolean enabled);
}
