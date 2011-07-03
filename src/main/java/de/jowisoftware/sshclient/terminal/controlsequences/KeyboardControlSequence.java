package de.jowisoftware.sshclient.terminal.controlsequences;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.SessionInfo;
import de.jowisoftware.sshclient.ui.GfxChar;

public class KeyboardControlSequence<T extends GfxChar> implements ControlSequence<T> {
    private static final Logger LOGGER = Logger.getLogger(KeyboardControlSequence.class);
    private static final Pattern pattern = Pattern.compile("(?:\\[\\?1[lh]|=|>)");
    private static final Pattern partialpattern = Pattern.compile("(?:\\[(?:\\?(?:1(?:[lh])?)?)?)");

    @Override
    public boolean isPartialStart(final CharSequence sequence) {
        return partialpattern.matcher(sequence).matches();
    }

    @Override
    public boolean canHandleSequence(final CharSequence sequence) {
        return pattern.matcher(sequence).matches();
    }

    @Override
    public void handleSequence(final String sequence,
            final SessionInfo<T> sessionInfo) {
        if (sequence.equals("[?1l")) {
            sessionInfo.getKeyboardFeedback().setCursorKeysIsAppMode(false);
        } else if (sequence.equals("[?1h")) {
            sessionInfo.getKeyboardFeedback().setCursorKeysIsAppMode(true);
        } else if (sequence.equals("=")) {
            sessionInfo.getKeyboardFeedback().setNumblockIsAppMode(true);
        } else if (sequence.equals(">")) {
            sessionInfo.getKeyboardFeedback().setNumblockIsAppMode(false);
        } else {
            LOGGER.error("Unknown control sequence: <ESC>" + sequence);
        }
    }

}
