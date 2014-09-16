package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.terminal.SSHSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class KeyboardControlSequence implements NonASCIIControlSequence {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyboardControlSequence.class);
    private static final Pattern PATTERN = Pattern.compile("=|>");

    @Override
    public boolean isPartialStart(final String sequence) {
        return false;
    }

    @Override
    public boolean canHandleSequence(final String sequence) {
        return PATTERN.matcher(sequence).matches();
    }

    @Override
    public void handleSequence(final String sequence,
            final SSHSession sessionInfo) {
        switch (sequence) {
            case "=":
                sessionInfo.getKeyboardFeedback().fire().newNumblockAppMode(true);
                break;
            case ">":
                sessionInfo.getKeyboardFeedback().fire().newNumblockAppMode(false);
                break;
            default:
                LOGGER.error("Unknown control sequence: <ESC>{}", sequence);
        }
    }
}
