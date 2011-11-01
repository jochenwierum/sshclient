package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.terminal.SSHSession;

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
        sessionInfo.getBuffer().addTabstopToCurrentPosition();
    }

}
