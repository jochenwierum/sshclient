package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.charsets.TerminalCharset;
import de.jowisoftware.sshclient.terminal.charsets.TerminalCharsetSelection;
import de.jowisoftware.sshclient.terminal.gfx.GfxCharSetup;

import java.util.regex.Pattern;

public class CharsetControlSequence implements NonASCIIControlSequence {
    private final static Pattern PATTERN = Pattern.compile("[NO]|[()][0AB]");
    private final static Pattern PATTERN_PARTIAL = Pattern.compile("[()]");

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
            final SSHSession sessionInfo) {
        final GfxCharSetup setup = sessionInfo.getCharSetup();
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
