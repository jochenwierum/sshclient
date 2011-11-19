package de.jowisoftware.sshclient.terminal.buffer;

public interface BoundaryLocator {
    Position findStartOfWord(Position position);
    Position findEndOfWord(Position position);
    void setSelectionChars(String string);
}
