package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.buffer.Position;

public class ANSISequenceCapitalHf<T extends GfxChar> implements ANSISequence<T> {
    @Override
    public void process(final Session<T> sessionInfo, final String... args) {
        int x = 1;
        int y = 1;

        if (args.length >= 1 && !args[0].equals("") && !args[0].equals("0")) {
            y = Integer.parseInt(args[0]);
        }
        if (args.length >= 2 && !args[1].equals("") && !args[1].equals("0")) {
            x = Integer.parseInt(args[1]);
        }

        sessionInfo.getBuffer().setCursorPosition(new Position(x, y));
    }
}
