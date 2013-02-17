package de.jowisoftware.sshclient.terminal.buffer;

import de.jowisoftware.sshclient.terminal.gfx.GfxChar;

public class Snapshot {
    public final Position cursorPosition;
    public final GfxChar[][] content;

    public Snapshot(final GfxChar[][] buffer, final Position cursorPosition) {
        this.content = buffer;
        this.cursorPosition = cursorPosition;
    }
}
