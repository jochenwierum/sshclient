package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Position;

public class ANSISequenceg implements ANSISequence {
    @Override
    public void process(final SSHSession sessionInfo, final String... args) {
        final int mod = args.length == 0 ? 0 : Integer.parseInt(args[0]);

        if (mod == 0) {
            final Position position = sessionInfo.getBuffer().getCursorPosition();
            sessionInfo.getTabStopManager().removeTab(position.x);
        } else if (mod == 3) {
            sessionInfo.getTabStopManager().removeAll();
        }
    }
}
