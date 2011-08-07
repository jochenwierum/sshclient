package de.jowisoftware.sshclient.terminal.controlsequences;

import java.util.regex.Pattern;

import de.jowisoftware.sshclient.terminal.GfxCharSetup;
import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.TerminalCharset;
import de.jowisoftware.sshclient.terminal.TerminalCharsetSelection;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

public class CharsetControlSequence<T extends GfxChar> implements
        NonASCIIControlSequence<T> {
    public final static Pattern PATTERN = Pattern.compile("[NO]|[()][0AB]");
    public final static Pattern PATTERN_PARTIAL = Pattern.compile("[()]");

    @Override
    public boolean isPartialStart(final String sequence) {
        return PATTERN_PARTIAL.matcher(sequence).matches();
    }

    @Override
    public boolean canHandleSequence(final String sequence) {
        return PATTERN.matcher(sequence).matches();
    }

    @Override
    public void handleSequence(final String sequence,
            final Session<T> sessionInfo) {
        final GfxCharSetup<T> setup = sessionInfo.getCharSetup();
        final char selectionCharacter = sequence.charAt(0);

        if (selectionCharacter == 'N') {
            setup.selectCharset(TerminalCharsetSelection.G1);
        } else if (selectionCharacter == 'O') {
            setup.selectCharset(TerminalCharsetSelection.G0);
        } else {
            final char charsetCharacter = sequence.charAt(1);
            setup.setCharset(
                    TerminalCharsetSelection.getByIdentifier(selectionCharacter),
                    TerminalCharset.getByIdentifier(charsetCharacter));
        }
    }
}
