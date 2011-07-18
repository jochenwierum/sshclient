package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

public class ANSISequenceL<T extends GfxChar> implements ANSISequence<T> {
    @Override
    public void process(final Session<T> sessionInfo, final String... args) {
        int lines = 1;
        if (args.length == 1) {
            lines = Integer.parseInt(args[0]);
        }

        sessionInfo.getBuffer().insertLines(lines);
    }
}
