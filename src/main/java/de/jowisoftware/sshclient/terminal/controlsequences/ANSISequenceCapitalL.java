package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Session;

public class ANSISequenceCapitalL implements ANSISequence {
    @Override
    public void process(final Session sessionInfo, final String... args) {
        int lines = 1;
        if (args.length == 1) {
            lines = Integer.parseInt(args[0]);
        }

        sessionInfo.getBuffer().insertLines(lines);
    }
}
