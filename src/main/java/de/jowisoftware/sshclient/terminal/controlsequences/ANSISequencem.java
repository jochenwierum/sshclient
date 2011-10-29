package de.jowisoftware.sshclient.terminal.controlsequences;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.Attribute;
import de.jowisoftware.sshclient.terminal.ColorFactory;
import de.jowisoftware.sshclient.terminal.GfxCharSetup;
import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.TerminalColor;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

public class ANSISequencem implements ANSISequence {
    private static final Logger LOGGER = Logger.getLogger(ANSISequencem.class);

    @Override
    public void process(final Session sessionInfo, final String... args) {
        if (args.length == 0) {
            processSequence(sessionInfo, 0);
        } else {
            for (final String seq : args) {
                processSequence(sessionInfo, Integer.parseInt(seq));
            }
        }
    }

    private void processSequence(final Session sessionInfo, final int seq) {
        if (seq == 0) {
            resetAttributes(sessionInfo);
        } else if (!processAttributes(sessionInfo, seq) &&
            !processDefaultColors(sessionInfo, seq)) {
                LOGGER.error("Unknown attribute: <ESC>[" + seq + "m");
        }
    }

    private void resetAttributes(final Session sessionInfo) {
        sessionInfo.getCharSetup().reset();
        final GfxChar clearChar = sessionInfo.getCharSetup().createClearChar();
        sessionInfo.getBuffer().setClearChar(clearChar);
    }

    private boolean processDefaultColors(final Session sessionInfo, final int seq) {
        final GfxCharSetup charSetup = sessionInfo.getCharSetup();
        final ColorFactory factory = charSetup.getColorFactory();
        final TerminalColor color = factory.createStandardColor(seq);

        if (color == null) {
            return false;
        }

        if (color.isForeground()) {
            charSetup.setForeground(color);
        } else {
            charSetup.setBackground(color);
            final GfxChar clearChar = charSetup.createClearChar();
            sessionInfo.getBuffer().setClearChar(clearChar);
        }
        return true;
    }

    private boolean processAttributes(final Session sessionInfo,
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
