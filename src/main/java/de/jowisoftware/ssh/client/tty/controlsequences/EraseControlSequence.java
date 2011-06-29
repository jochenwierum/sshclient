package de.jowisoftware.ssh.client.tty.controlsequences;

import java.util.regex.Pattern;

import de.jowisoftware.ssh.client.tty.Buffer;
import de.jowisoftware.ssh.client.tty.GfxCharSetup;
import de.jowisoftware.ssh.client.ui.GfxChar;

public class EraseControlSequence<T extends GfxChar> implements
        ControlSequence<T> {

    private static final Pattern pattern = Pattern
            .compile("\\[J");
    private static final Pattern partialpattern = Pattern
            .compile("\\[");

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
        buffer.eraseDown();
    }
}
