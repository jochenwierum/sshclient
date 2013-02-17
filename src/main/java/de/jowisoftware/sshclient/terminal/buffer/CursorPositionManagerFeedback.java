package de.jowisoftware.sshclient.terminal.buffer;

interface CursorPositionManagerFeedback {
    void lineShiftingNeeded(int offset, int start, int end);
}
