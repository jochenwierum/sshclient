package de.jowisoftware.sshclient.terminal.controlsequences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.SessionInfo;
import de.jowisoftware.sshclient.ui.GfxChar;

public class EraseControlSequence<T extends GfxChar> implements
        ControlSequence<T> {
    private static final Pattern pattern = Pattern.compile("\\[([12])?(J|K)");
    private static final Pattern partialpattern = Pattern.compile("\\[[12]?");
    private static final Logger LOGGER = Logger.getLogger(EraseControlSequence.class);

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
        final Matcher matcher = pattern.matcher(sequence);
        matcher.matches();

        final char command = matcher.group(2).charAt(0);
        final char mod = matcher.group(1) == null ? '0' : matcher.group(1).charAt(0);

        if (command == 'J' && mod == '0') {
            sessionInfo.getBuffer().eraseToBottom();
        } else if(command == 'J' && mod == '1') {
            sessionInfo.getBuffer().eraseFromTop();
        } else if (command == 'J' && mod == '2') {
            sessionInfo.getBuffer().erase();
        } else if (command == 'K' && mod == '0') {
            sessionInfo.getBuffer().eraseRestOfLine();
        }  else if (command == 'K' && mod == '1') {
            sessionInfo.getBuffer().eraseStartOfLine();
        } else if (command == 'K' && mod == '2') {
            sessionInfo.getBuffer().eraseLine();
        } else {
            LOGGER.error("Unknown control sequence: <ESC>" + sequence);
        }
    }
}
