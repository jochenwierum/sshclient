package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Position;
import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.ui.GfxChar;

public class ANSISequencer<T extends GfxChar> implements ANSISequence<T> {
    @Override
    public void process(final Session<T> sessionInfo, final String... args) {
        if (args.length == 2) {
            sessionInfo.getBuffer().setMargin(
                    Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        } else {
            sessionInfo.getBuffer().resetMargin();
        }
        sessionInfo.getBuffer().setCursorPosition(new Position(1, 1));
    }
}
