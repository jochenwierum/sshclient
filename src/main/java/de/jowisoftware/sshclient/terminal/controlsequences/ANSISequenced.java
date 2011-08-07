package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.buffer.Position;

public class ANSISequenced<T extends GfxChar> implements ANSISequence<T> {
    @Override
    public void process(final Session<T> sessionInfo, final String... args) {
        int y = 1;

        if (args.length >= 1 && !args[0].equals("") && !args[0].equals("0")) {
            y = Integer.parseInt(args[0]);
        }

        final Position position = sessionInfo.getBuffer().getCursorPosition();
        sessionInfo.getBuffer().setCursorPosition(position.withY(y));
    }
}
