package de.jowisoftware.ssh.client.tty.controlsequences;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.jowisoftware.ssh.client.tty.Buffer;
import de.jowisoftware.ssh.client.tty.GfxCharSetup;
import de.jowisoftware.ssh.client.tty.GfxCharSetup.Attributes;
import de.jowisoftware.ssh.client.tty.GfxCharSetup.Colors;
import de.jowisoftware.ssh.client.ui.GfxChar;

public class DisplayAttributeControlSequence<T extends GfxChar> implements ControlSequence<T> {
    private static final Pattern pattern = Pattern.compile("\\[(?:\\d+;)*(?:\\d+)m");
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
        final String[] numbers = sequence.substring(1, sequence.length() - 1).split(";");

        for (final String number : numbers) {
            switch (Integer.parseInt(number)) {
            case 0: setup.reset(); break;
            case 1: setup.setAttribute(Attributes.BRIGHT); break;
            case 2: setup.setAttribute(Attributes.DIM); break;
            case 4: setup.setAttribute(Attributes.UNDERSCORE); break;
            case 5: setup.setAttribute(Attributes.BLINK); break;
            case 7: setup.setAttribute(Attributes.REVERSE); break;
            case 8: setup.setAttribute(Attributes.HIDDEN); break;
            case 30: setup.setForeground(Colors.BLACK); break;
            case 31: setup.setForeground(Colors.RED); break;
            case 32: setup.setForeground(Colors.GREEN); break;
            case 33: setup.setForeground(Colors.YELLOW); break;
            case 34: setup.setForeground(Colors.BLUE); break;
            case 35: setup.setForeground(Colors.MAGENTA); break;
            case 36: setup.setForeground(Colors.CYAN); break;
            case 37: setup.setForeground(Colors.WHITE); break;
            case 40: setup.setBackground(Colors.BLACK); break;
            case 41: setup.setBackground(Colors.RED); break;
            case 42: setup.setBackground(Colors.GREEN); break;
            case 43: setup.setBackground(Colors.YELLOW); break;
            case 44: setup.setBackground(Colors.BLUE); break;
            case 45: setup.setBackground(Colors.MAGENTA); break;
            case 46: setup.setBackground(Colors.CYAN); break;
            case 47: setup.setBackground(Colors.WHITE); break;
            default: LOGGER.warn("Unknown attribute: <ESC>[" + number + "m");
            }
        }
    }
}
