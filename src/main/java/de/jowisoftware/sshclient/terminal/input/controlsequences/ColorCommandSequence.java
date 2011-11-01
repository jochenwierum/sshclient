package de.jowisoftware.sshclient.terminal.input.controlsequences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.jowisoftware.sshclient.terminal.SSHSession;

public class ColorCommandSequence implements NonASCIIControlSequence {
    private final static Pattern partialStart = Pattern.compile(
            "\\]4;(\\d{2}|1\\d{2}|2[01234]\\d|25[012345]);" +
            "rgb:([0-9a-fA-F]{2})/([0-9a-fA-F]{2})/([0-9a-fA-F]{2})\u001b\\\\");

    @Override
    public boolean isPartialStart(final String sequence) {
        final Matcher match = partialStart.matcher(sequence);
        return match.matches() || match.hitEnd();
    }

    @Override
    public boolean canHandleSequence(final String sequence) {
        final Matcher match = partialStart.matcher(sequence);
        return match.matches();
    }

    @Override
    public void handleSequence(final String sequence, final SSHSession sessionInfo) {
        final Matcher matcher = partialStart.matcher(sequence);
        matcher.find();
        final int colorNumber = Integer.valueOf(matcher.group(1));
        final int r = Integer.valueOf(matcher.group(2).toUpperCase(), 16);
        final int g = Integer.valueOf(matcher.group(3).toUpperCase(), 16);
        final int b = Integer.valueOf(matcher.group(4).toUpperCase(), 16);

        sessionInfo.getCharSetup().updateCustomColor(colorNumber, r, g, b);
    }
}
