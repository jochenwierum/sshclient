package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.buffer.Range;

public class ANSISequenceCapitalX implements ANSISequence {
    @Override
    public void process(final Session sessionInfo, final String... args) {
        int length = 1;

        if (args.length == 1 && !args[0].equals("0")) {
            length = Integer.parseInt(args[0]);
        }

        final Buffer buffer = sessionInfo.getBuffer();
        final Position position = buffer.getCursorPosition();

        buffer.erase(new Range(position, position.offset(length, 0)));
    }
}
