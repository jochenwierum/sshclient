package de.jowisoftware.sshclient.terminal.events;

public interface KeyboardEvent {
    void newCursorKeysIsAppMode(final boolean value);
    void newNumblockAppMode(boolean enabled);
}
