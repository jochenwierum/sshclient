package de.jowisoftware.sshclient.terminal.controlsequences;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.Attribute;
import de.jowisoftware.sshclient.terminal.Color;
import de.jowisoftware.sshclient.terminal.SessionInfo;
import de.jowisoftware.sshclient.ui.GfxChar;

public class DisplayAttributeControlSequence<T extends GfxChar> implements ControlSequence<T> {
    private static final Logger LOGGER = Logger.getLogger(DisplayAttributeControlSequence.class);
    private static final Pattern pattern = Pattern.compile("\\[(?:(?:\\d+;)*(?:\\d+))?m");
    private static final Pattern partialpattern = Pattern.compile("\\[(?:\\d+;?)*");

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
        if (sequence.equals("[m") || sequence.equals("[0m")) {
            sessionInfo.getCharSetup().reset();
            return;
        }

        final String[] numbers = sequence.substring(1, sequence.length() - 1).split(";");

        sequence:
        for (final String number : numbers) {
            final int seq = Integer.parseInt(number);

            if (seq == 0) {
                sessionInfo.getCharSetup().reset();
                continue sequence;
            }

            for (final Attribute attr : Attribute.values()) {
                if (attr.isActivateSequence(seq)) {
                    sessionInfo.getCharSetup().setAttribute(attr);
                    continue sequence;
                } else if (attr.isDeactivateSequence(seq)) {
                    sessionInfo.getCharSetup().removeAttribute(attr);
                    continue sequence;
                }
            }

            for (final Color color : Color.values()) {
                if (color.isForegroundSequence(seq)) {
                    sessionInfo.getCharSetup().setForeground(color);
                    continue sequence;
                } else if (color.isBackgroundSequence(seq)) {
                    sessionInfo.getCharSetup().setBackground(color);
                    continue sequence;
                }
            }

            LOGGER.error("Unknown attribute: <ESC>[" + number + "m");
        }
    }
}
