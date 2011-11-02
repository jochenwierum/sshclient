package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Position;

public class TabstopSequence implements NonASCIIControlSequence {

    @Override
    public boolean isPartialStart(final String sequence) {
        return false;
    }

    @Override
    public boolean canHandleSequence(final String sequence) {
        return "H".equals(sequence);
    }

    @Override
    public void handleSequence(final String sequence, final SSHSession sessionInfo) {
        final Position position = sessionInfo.getBuffer().getCursorPosition();
        sessionInfo.getTabStopManager().addTab(position.x);
    }

}
