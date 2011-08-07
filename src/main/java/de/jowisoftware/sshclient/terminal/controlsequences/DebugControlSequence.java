package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.buffer.Position;

public class DebugControlSequence<T extends GfxChar> implements NonASCIIControlSequence<T> {

    @Override
    public boolean isPartialStart(final String sequence) {
        return sequence.equals("#");
    }

    @Override
    public boolean canHandleSequence(final String sequence) {
        return sequence.equals("#8");
    }

    @Override
    public void handleSequence(final String sequence, final Session<T> sessionInfo) {
        sessionInfo.getCharSetup().reset();
        final T gfxChar = sessionInfo.getCharSetup().createChar('E');

        final Buffer<T> buffer = sessionInfo.getBuffer();
        final Position size = buffer.getSize();

        buffer.setCursorPosition(new Position(1, 1));
        for (int i = 1; i <= size.y; ++i) {
            for (int j = 1; j <= size.x; ++j) {
                buffer.addCharacter(gfxChar);
            }
            if (i < size.x) {
                buffer.addNewLine();
            }
        }
    }
}
