package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Position;
import de.jowisoftware.sshclient.terminal.SessionInfo;
import de.jowisoftware.sshclient.ui.GfxChar;

public class ANSISequenceABCD<T extends GfxChar> implements ANSISequence<T> {
    private final int dx;
    private final int dy;

    public ANSISequenceABCD(final int dx, final int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void process(final SessionInfo<T> sessionInfo, final String... args) {
        int count = 1;
        if (args.length == 1 && !args[0].equals("1")) {
            count = Integer.parseInt(args[0]);
        }

        final Position newPosition = sessionInfo.getBuffer()
                .getCursorPosition().offset(count * dx, count * dy);
        sessionInfo.getBuffer().setCursorPosition(newPosition);
    }
}
