package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.CursorPosition;
import de.jowisoftware.sshclient.terminal.SessionInfo;
import de.jowisoftware.sshclient.ui.GfxChar;

public class ANSISequencer<T extends GfxChar> implements ANSISequence<T> {
    @Override
    public void process(final SessionInfo<T> sessionInfo, final String... args) {
        if (args.length == 2) {
            sessionInfo.getBuffer().setRollRange(
                    Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        } else {
            sessionInfo.getBuffer().deleteRollRange();
        }
        sessionInfo.getBuffer().setCursorPosition(new CursorPosition(1, 1));
    }
}
