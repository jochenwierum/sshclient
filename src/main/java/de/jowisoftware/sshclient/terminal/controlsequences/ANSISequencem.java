package de.jowisoftware.sshclient.terminal.controlsequences;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.Attribute;
import de.jowisoftware.sshclient.terminal.Color;
import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.ui.GfxChar;

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
            sessionInfo.getCharSetup().reset();
            return;
        }

        if (!processAttributes(sessionInfo, seq) &&
            !processColors(sessionInfo, seq)) {
                LOGGER.error("Unknown attribute: <ESC>[" + seq + "m");
        }
    }

    private boolean processColors(final Session<T> sessionInfo, final int seq) {
        for (final Color color : Color.values()) {
            if (color.isForegroundSequence(seq)) {
                sessionInfo.getCharSetup().setForeground(color);
                return true;
            } else if (color.isBackgroundSequence(seq)) {
                sessionInfo.getCharSetup().setBackground(color);
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
