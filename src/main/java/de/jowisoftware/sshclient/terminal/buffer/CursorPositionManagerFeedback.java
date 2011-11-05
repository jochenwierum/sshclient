package de.jowisoftware.sshclient.terminal.buffer;

public interface CursorPositionManagerFeedback {
    void lineShiftingNeeded(int offset, int start, int end);
}
