package de.jowisoftware.ssh.client.terminal.controlsequences;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.jowisoftware.ssh.client.terminal.Buffer;
import de.jowisoftware.ssh.client.terminal.GfxCharSetup;
import de.jowisoftware.ssh.client.terminal.KeyboardFeedback;
import de.jowisoftware.ssh.client.ui.GfxChar;

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
    public void handleSequence(final String sequence, final Buffer<T> buffer,
            final GfxCharSetup<T> setup, final KeyboardFeedback keyboardFeedback) {
        if (sequence.equals("[?1l")) {
            keyboardFeedback.setCursorKeysIsAppMode(false);
        } else if (sequence.equals("[?1h")) {
            keyboardFeedback.setCursorKeysIsAppMode(true);
        } else if (sequence.equals("=")) {
            keyboardFeedback.setNumblockIsAppMode(true);
        } else if (sequence.equals(">")) {
            keyboardFeedback.setNumblockIsAppMode(false);
        } else {
            LOGGER.error("Unknown control sequence: <ESC>" + sequence);
        }
    }

}
