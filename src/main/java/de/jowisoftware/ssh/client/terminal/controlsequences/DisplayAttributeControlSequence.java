package de.jowisoftware.ssh.client.terminal.controlsequences;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.jowisoftware.ssh.client.terminal.Attribute;
import de.jowisoftware.ssh.client.terminal.Buffer;
import de.jowisoftware.ssh.client.terminal.Color;
import de.jowisoftware.ssh.client.terminal.GfxCharSetup;
import de.jowisoftware.ssh.client.ui.GfxChar;

public class DisplayAttributeControlSequence<T extends GfxChar> implements ControlSequence<T> {
    private static final Pattern pattern = Pattern.compile("\\[(?:(?:\\d+;)*(?:\\d+))?m");
    private static final Pattern partialpattern = Pattern.compile("\\[(?:\\d+;?)*");
    private static final Logger LOGGER = Logger.getLogger(DisplayAttributeControlSequence.class);

    @Override
    public boolean isPartialStart(final CharSequence sequence) {
        return partialpattern.matcher(sequence).matches();
    }

    @Override
    public boolean canHandleSequence(final CharSequence sequence) {
        return pattern.matcher(sequence).matches();
    }

    @Override
    public void handleSequence(final String sequence, final Buffer<T> buffer, final GfxCharSetup<T> setup) {
        if (sequence.equals("[m") || sequence.equals("[0m")) {
            setup.reset();
            return;
        }

        final String[] numbers = sequence.substring(1, sequence.length() - 1).split(";");

        sequence:
        for (final String number : numbers) {
            final int seq = Integer.parseInt(number);

            for (final Attribute attr : Attribute.values()) {
                if (attr.isActivateSequence(seq)) {
                    setup.setAttribute(attr);
                    continue sequence;
                } else if (attr.isDeactivateSequence(seq)) {
                    setup.removeAttribute(attr);
                    continue sequence;
                }
            }

            for (final Color color : Color.values()) {
                if (color.isForegroundSequence(seq)) {
                    setup.setForeground(color);
                    continue sequence;
                } else if (color.isBackgroundSequence(seq)) {
                    setup.setBackground(color);
                    continue sequence;
                }
            }

            LOGGER.warn("Unknown attribute: <ESC>[" + number + "m");
        }
    }
}
