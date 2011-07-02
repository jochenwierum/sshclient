package de.jowisoftware.ssh.client.terminal.controlsequences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.jowisoftware.ssh.client.terminal.Buffer;
import de.jowisoftware.ssh.client.terminal.CursorPosition;
import de.jowisoftware.ssh.client.terminal.GfxCharSetup;
import de.jowisoftware.ssh.client.terminal.KeyboardFeedback;
import de.jowisoftware.ssh.client.ui.GfxChar;

public class CursorControlSequence<T extends GfxChar> implements
        ControlSequence<T> {
    private static final Logger LOGGER = Logger.getLogger(CursorControlSequence.class);
    private static final Pattern pattern = Pattern
            .compile("\\[(?:\\d+;\\d+)?[Hr]");
    private static final Pattern partialpattern = Pattern
            .compile("\\[(?:\\d+|\\d+;|\\d+;\\d+)?");

    @Override
    public boolean isPartialStart(final CharSequence sequence) {
        return partialpattern.matcher(sequence).matches();
    }

    @Override
    public boolean canHandleSequence(final CharSequence sequence) {
        return pattern.matcher(sequence).matches();
    }

    @Override
    public void handleSequence(final String sequence, final Buffer<T> buffer,
            final GfxCharSetup<T> setup, final KeyboardFeedback feedback) {
        final Matcher matcher = pattern.matcher(sequence);
        matcher.matches();

        final char lastChar = sequence.charAt(sequence.length() - 1);

        switch(lastChar) {
        case 'H': processMoveCursor(buffer, sequence); break;
        case 'r': processRollSetup(buffer, sequence); break;
        default:
            LOGGER.error("Unknown control sequence: <ESC>" + sequence);
        }
    }

    private void processRollSetup(final Buffer<T> buffer, final String sequence) {
        if (sequence.length() > 2) {
            final String yy[] = sequence.substring(1, sequence.length() - 1).split(";");
            final int y1 = Integer.parseInt(yy[0]);
            final int y2 = Integer.parseInt(yy[1]);
            buffer.setRollRange(y1, y2);
        } else {
            buffer.deleteRollRange();
        }
        buffer.setCursorPosition(new CursorPosition(1, 1));
    }

    private void processMoveCursor(final Buffer<T> buffer, final String sequence) {
        if (sequence.length() > 2) {
            final String xy[] = sequence.substring(1, sequence.length() - 1).split(";");
            final int x = Integer.parseInt(xy[1]);
            final int y = Integer.parseInt(xy[0]);
            buffer.setCursorPosition(new CursorPosition(x, y));
        } else {
            buffer.setAbsoluteCursorPosition(new CursorPosition(1, 1));
        }
    }
}
