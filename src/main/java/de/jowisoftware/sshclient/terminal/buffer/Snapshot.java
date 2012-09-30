package de.jowisoftware.sshclient.terminal.buffer;

import de.jowisoftware.sshclient.terminal.gfx.GfxChar;

public class Snapshot {
    public Position cursorPosition;
    public GfxChar[][] content;

    public Snapshot(final GfxChar[][] buffer, final Position cursorPosition) {
        this.content = buffer;
        this.cursorPosition = cursorPosition;
    }
}
