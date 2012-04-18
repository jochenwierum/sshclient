package de.jowisoftware.sshclient.ui.terminal;

import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.buffer.Position;

public class Snapshot {
    public Position cursorPosition;
    public GfxChar[][] content;

    public Snapshot(final GfxChar[][] buffer, final Position cursorPosition) {
        this.content = buffer;
        this.cursorPosition = cursorPosition;
    }
}
