package de.jowisoftware.sshclient.terminal;

public interface KeyboardEvent {
    void newCursorKeysIsAppMode(final boolean value);
    void newNumblockAppMode(boolean enabled);
}
