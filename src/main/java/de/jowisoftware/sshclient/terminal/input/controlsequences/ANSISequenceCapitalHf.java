package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Position;

public class ANSISequenceCapitalHf implements ANSISequence {
    @Override
    public void process(final SSHSession sessionInfo, final String... args) {
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
