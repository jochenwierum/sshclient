package de.jowisoftware.sshclient.terminal.controlsequences;

import java.util.regex.Pattern;

import de.jowisoftware.sshclient.terminal.SessionInfo;
import de.jowisoftware.sshclient.ui.GfxChar;

public class OperatingSystemCommandSequence<T extends GfxChar> implements NonASCIIControlSequence<T> {
    private static final Pattern pattern = Pattern.compile("\\][02];.*\u0007");
    private static final Pattern partialpattern = Pattern.compile("(?:\\](?:[02](?:;.*)?)?)");

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
        final String title = sequence.substring(3, sequence.length() - 1);
        sessionInfo.getVisualFeedback().setTitle(title);
    }
}
