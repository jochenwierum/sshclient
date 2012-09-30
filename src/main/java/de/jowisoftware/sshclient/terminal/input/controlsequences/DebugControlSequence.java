package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.gfx.GfxChar;

public class DebugControlSequence implements NonASCIIControlSequence {

    @Override
    public boolean isPartialStart(final String sequence) {
        return sequence.equals("#");
    }

    @Override
    public boolean canHandleSequence(final String sequence) {
        return sequence.equals("#8");
    }

    @Override
    public void handleSequence(final String sequence, final SSHSession sessionInfo) {
        sessionInfo.getCharSetup().reset();
        final GfxChar gfxChar = sessionInfo.getCharSetup().createChar('E');

        final Buffer buffer = sessionInfo.getBuffer();
        final Position size = buffer.getSize();

        buffer.setCursorPosition(new Position(1, 1));
        for (int i = 1; i <= size.y; ++i) {
            for (int j = 1; j <= size.x; ++j) {
                buffer.addCharacter(gfxChar);
            }
            if (i < size.x) {
                buffer.moveCursorDown(true);
            }
        }
    }
}
