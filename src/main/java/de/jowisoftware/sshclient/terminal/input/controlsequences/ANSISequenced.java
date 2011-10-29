package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Position;

public class ANSISequenced implements ANSISequence {
    @Override
    public void process(final SSHSession sessionInfo, final String... args) {
        int y = 1;

        if (args.length >= 1 && !args[0].equals("") && !args[0].equals("0")) {
            y = Integer.parseInt(args[0]);
        }

        final Position position = sessionInfo.getBuffer().getCursorPosition();
        sessionInfo.getBuffer().setCursorPosition(position.withY(y));
    }
}
