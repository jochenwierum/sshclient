package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

public class ANSISequenceAt<T extends GfxChar> implements ANSISequence<T> {
    @Override
    public void process(final Session<T> sessionInfo, final String... args) {
        int length = 1;

        if (args.length == 1 && !args[0].equals("0")) {
            length = Integer.parseInt(args[0]);
        }

        final Buffer<T> buffer = sessionInfo.getBuffer();
        buffer.shift(-length);
    }
}
