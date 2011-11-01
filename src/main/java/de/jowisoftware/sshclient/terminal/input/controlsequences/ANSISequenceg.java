package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.terminal.SSHSession;

public class ANSISequenceg implements ANSISequence {
    @Override
    public void process(final SSHSession sessionInfo, final String... args) {
        final int mod = args.length == 0 ? 0 : Integer.parseInt(args[0]);

        if (mod == 0) {
            sessionInfo.getBuffer().removeTabstopAtCurrentPosition();
        } else if (mod == 3) {
            sessionInfo.getBuffer().removeTabstops();
        }
    }
}
