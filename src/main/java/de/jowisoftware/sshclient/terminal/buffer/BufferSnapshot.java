package de.jowisoftware.sshclient.terminal.buffer;

public class BufferSnapshot {
    public final GfxChar[][] content;
    public final Position cursorPosition;

    public BufferSnapshot(final GfxChar[][] content, final Position cursorPosition) {
        this.content = content;
        this.cursorPosition = cursorPosition;
    }
}
