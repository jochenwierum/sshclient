package de.jowisoftware.ssh.client.tty.controlsequences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.jowisoftware.ssh.client.tty.Buffer;
import de.jowisoftware.ssh.client.tty.GfxCharSetup;
import de.jowisoftware.ssh.client.ui.GfxChar;

public class EraseControlSequence<T extends GfxChar> implements
        ControlSequence<T> {

    private static final Pattern pattern = Pattern
            .compile("\\[([12])?(J|K)");
    private static final Pattern partialpattern = Pattern
            .compile("\\[[12]?");

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

        final char command = matcher.group(2).charAt(0);
        final char mod = matcher.group(1) == null ? '0' : matcher.group(1).charAt(0);

        if (command == 'J' && mod == '0') {
            buffer.eraseToBottom();
        } else if(command == 'J' && mod == '1') {
            buffer.eraseFromTop();
        } else if (command == 'J' && mod == '2') {
            buffer.erase();
        } else if (command == 'K' && mod == '0') {
            buffer.eraseRestOfLine();
        }  else if (command == 'K' && mod == '1') {
            buffer.eraseStartOfLine();
        } else if (command == 'K' && mod == '2') {
            buffer.eraseLine();
        }
    }
}
