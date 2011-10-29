package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.buffer.Position;

public class ANSISequenceCapitalG implements ANSISequence {
    @Override
    public void process(final Session sessionInfo, final String... args) {
        int x = 1;

        if (args.length >= 1 && !args[0].equals("") && !args[0].equals("0")) {
            x = Integer.parseInt(args[0]);
        }

        final Position position = sessionInfo.getBuffer().getCursorPosition();
        sessionInfo.getBuffer().setCursorPosition(position.withX(x));
    }
}
