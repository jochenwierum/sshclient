package de.jowisoftware.sshclient.terminal.controlsequences;

import java.util.regex.Pattern;

import de.jowisoftware.sshclient.terminal.Session;

public class OperatingSystemCommandSequence implements NonASCIIControlSequence {
    private static final Pattern PATTERN = Pattern.compile("\\][012];.*\u0007");
    private static final Pattern PARTIAL_PATTERN = Pattern.compile("(?:\\](?:[012](?:;.*)?)?)");

    @Override
    public boolean isPartialStart(final String sequence) {
        return PARTIAL_PATTERN.matcher(sequence).matches();
    }

    @Override
    public boolean canHandleSequence(final String sequence) {
        return PATTERN.matcher(sequence).matches();
    }

    @Override
    public void handleSequence(final String sequence,
            final Session sessionInfo) {
        final String title = sequence.substring(3, sequence.length() - 1);
        sessionInfo.getVisualFeedback().fire().newTitle(title);
    }
}
