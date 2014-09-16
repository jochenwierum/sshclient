package de.jowisoftware.sshclient.terminal.charsets;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum TerminalCharset {
    DECCHARS('0'),
    UK('A'),
    USASCII('B');

    private static final Logger LOGGER = LoggerFactory
            .getLogger(TerminalCharset.class);

    private final char identifier;
    private TerminalCharset(final char identifier) {
        this.identifier = identifier;
    }

    public static TerminalCharset getByIdentifier(final char c) {
        for (final TerminalCharset charset : TerminalCharset.values()) {
            if (charset.identifier == c) {
                return charset;
            }
        }

        LOGGER.warn("Looking up illegal charset: {}", c);
        return USASCII;
    }
}
