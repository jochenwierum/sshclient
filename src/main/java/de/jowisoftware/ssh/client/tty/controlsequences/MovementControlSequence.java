package de.jowisoftware.ssh.client.tty.controlsequences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.jowisoftware.ssh.client.tty.Buffer;
import de.jowisoftware.ssh.client.tty.CursorPosition;
import de.jowisoftware.ssh.client.tty.GfxCharSetup;
import de.jowisoftware.ssh.client.ui.GfxChar;

public class MovementControlSequence<T extends GfxChar> implements
        ControlSequence<T> {

    private static final Pattern pattern = Pattern
            .compile("\\[(\\d+;\\d+)?H");
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
            final GfxCharSetup<T> setup) {
        final Matcher matcher = pattern.matcher(sequence);
        matcher.matches();

        int x = 0;
        int y = 0;
        if (matcher.group(1) != null) {
            final String xy[] = matcher.group(1).split(";");
            x = Integer.parseInt(xy[0]);
            y = Integer.parseInt(xy[1]);
        }

        buffer.setCursorPosition(new CursorPosition(x, y));
    }
}
