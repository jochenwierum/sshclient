package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Position;
import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.ui.GfxChar;

public class ANSISequenceH<T extends GfxChar> implements ANSISequence<T> {
    @Override
    public void process(final Session<T> sessionInfo, final String... args) {
        if (args.length == 2) {
            sessionInfo.getBuffer().setCursorPosition(new Position(
                    Integer.parseInt(args[1]), Integer.parseInt(args[0])));
        } else {
            sessionInfo.getBuffer().setAbsoluteCursorPosition(new Position(1, 1));
        }
    }
}
