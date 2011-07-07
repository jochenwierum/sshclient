package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.ui.GfxChar;

public class ANSISequenceHighLow<T extends GfxChar> implements ANSISequence<T> {
    private final boolean isHigh;

    public ANSISequenceHighLow(final boolean isHigh) {
        this.isHigh = isHigh;
    }

    @Override
    public void process(final Session<T> sessionInfo, final String... args) {
        if (args[0].equals("?1")) {
            sessionInfo.getKeyboardFeedback().setCursorKeysIsAppMode(isHigh);
        }
    }
}
