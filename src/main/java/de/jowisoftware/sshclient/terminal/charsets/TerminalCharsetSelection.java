package de.jowisoftware.sshclient.terminal.charsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum TerminalCharsetSelection {
    G0('('), G1(')');

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalCharsetSelection.class);

    private final char identifier;
    private TerminalCharsetSelection(final char identifier) {
        this.identifier = identifier;
    }

    public static TerminalCharsetSelection getByIdentifier(
            final char selectionCharacter) {
        for (final TerminalCharsetSelection selection :
                TerminalCharsetSelection.values()) {
            if (selection.identifier == selectionCharacter) {
                return selection;
            }
        }

        LOGGER.warn("Looking up illegal charset: {}", selectionCharacter);
        return G0;
    }
}
