package de.jowisoftware.sshclient.terminal.controlsequences;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.Attribute;
import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.TerminalColor;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

public class ANSISequencem<T extends GfxChar> implements ANSISequence<T> {
    private static final Logger LOGGER = Logger.getLogger(ANSISequencem.class);

    @Override
    public void process(final Session<T> sessionInfo, final String... args) {
        if (args.length == 0) {
            processSequence(sessionInfo, 0);
        } else {
            for (final String seq : args) {
                processSequence(sessionInfo, Integer.parseInt(seq));
            }
        }
    }

    private void processSequence(final Session<T> sessionInfo, final int seq) {
        if (seq == 0) {
            resetAttributes(sessionInfo);
        } else if (!processAttributes(sessionInfo, seq) &&
            !processColors(sessionInfo, seq)) {
                LOGGER.error("Unknown attribute: <ESC>[" + seq + "m");
        }
    }

    private void resetAttributes(final Session<T> sessionInfo) {
        sessionInfo.getCharSetup().reset();
        final T clearChar = sessionInfo.getCharSetup().createClearChar();
        sessionInfo.getBuffer().setClearChar(clearChar);
    }

    private boolean processColors(final Session<T> sessionInfo, final int seq) {
        for (final TerminalColor color : TerminalColor.values()) {
            if (color.isForegroundSequence(seq)) {
                sessionInfo.getCharSetup().setForeground(color);
                return true;
            } else if (color.isBackgroundSequence(seq)) {
                sessionInfo.getCharSetup().setBackground(color);
                final T clearChar = sessionInfo.getCharSetup().createClearChar();
                sessionInfo.getBuffer().setClearChar(clearChar);
                return true;
            }
        }
        return false;
    }

    private boolean processAttributes(final Session<T> sessionInfo,
            final int seq) {
        for (final Attribute attr : Attribute.values()) {
            if (attr.isActivateSequence(seq)) {
                sessionInfo.getCharSetup().setAttribute(attr);
                return true;
            } else if (attr.isDeactivateSequence(seq)) {
                sessionInfo.getCharSetup().removeAttribute(attr);
                return true;
            }
        }
        return false;
    }
}
