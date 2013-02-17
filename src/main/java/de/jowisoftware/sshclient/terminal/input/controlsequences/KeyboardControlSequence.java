package de.jowisoftware.sshclient.terminal.input.controlsequences;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.SSHSession;

public class KeyboardControlSequence implements NonASCIIControlSequence {
    private static final Logger LOGGER = Logger.getLogger(KeyboardControlSequence.class);
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
                LOGGER.error("Unknown control sequence: <ESC>" + sequence);
        }
    }
}
