package de.jowisoftware.sshclient.terminal.controlsequences;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.ui.GfxChar;

public class KeyboardControlSequence<T extends GfxChar> implements NonASCIIControlSequence<T> {
    private static final Logger LOGGER = Logger.getLogger(KeyboardControlSequence.class);
    private static final Pattern pattern = Pattern.compile("=|>");

    @Override
    public boolean isPartialStart(final CharSequence sequence) {
        return false;
    }

    @Override
    public boolean canHandleSequence(final CharSequence sequence) {
        return pattern.matcher(sequence).matches();
    }

    @Override
    public void handleSequence(final String sequence,
            final Session<T> sessionInfo) {
        if (sequence.equals("=")) {
            sessionInfo.getKeyboardFeedback().setNumblockIsAppMode(true);
        } else if (sequence.equals(">")) {
            sessionInfo.getKeyboardFeedback().setNumblockIsAppMode(false);
        } else {
            LOGGER.error("Unknown control sequence: <ESC>" + sequence);
        }
    }
}
