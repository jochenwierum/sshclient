package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Position;

public class ANSISequencer implements ANSISequence {
    @Override
    public void process(final SSHSession sessionInfo, final String... args) {
        if (args.length == 2) {
            sessionInfo.getBuffer().setMargin(
                    Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        } else {
            sessionInfo.getBuffer().resetMargin();
        }
        sessionInfo.getBuffer().setCursorPosition(new Position(1, 1));
    }
}
