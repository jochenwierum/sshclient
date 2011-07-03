package de.jowisoftware.sshclient.terminal.controlsequences;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.Buffer;
import de.jowisoftware.sshclient.terminal.CursorPosition;
import de.jowisoftware.sshclient.terminal.SessionInfo;
import de.jowisoftware.sshclient.ui.GfxChar;

// TODO too complicate? Refactor?
public class CursorControlSequence<T extends GfxChar> implements
        ControlSequence<T> {
    private static final Logger LOGGER = Logger.getLogger(CursorControlSequence.class);
    private static final Pattern pattern = Pattern
            .compile("\\[(?:(?:\\d+;\\d+)?[Hr]|\\d*[ABCD])|[DEM]");
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
    public void handleSequence(final String sequence,
            final SessionInfo<T> sessionInfo) {

        final char lastChar = sequence.charAt(sequence.length() - 1);

        if (sequence.equals("D") || sequence.equals("E") || sequence.endsWith("M")) {
            processRollCursor(sessionInfo.getBuffer(), sequence);
        } else if (lastChar == 'H') {
            processSetCursor(sessionInfo.getBuffer(), sequence);
        } else if(lastChar == 'r') {
            processRollSetup(sessionInfo.getBuffer(), sequence);
        } else if(lastChar >= 'A' && lastChar <= 'D') {
            processMoveCursor(sessionInfo.getBuffer(), sequence);
        } else {
            LOGGER.error("Unknown control sequence: <ESC>" + sequence);
        }
    }

    private void processRollCursor(final Buffer<T> buffer, final String sequence) {
        if (sequence.equals("D")) {
            buffer.moveCursorDownAndRoll(false);
        } else if (sequence.endsWith("E")) {
            buffer.moveCursorDownAndRoll(true);
        } else if (sequence.endsWith("M")) {
            buffer.moveCursorUpAndRoll();
        }
    }

    private void processMoveCursor(final Buffer<T> buffer, final String sequence) {
        int count = 1;
        final String countString = sequence.substring(1, sequence.length() - 1);
        if (!countString.equals("") && !countString.equals("0")) {
            count = Integer.parseInt(countString);
        }

        switch(sequence.charAt(sequence.length() - 1)) {
        case 'A': buffer.setSafeCursorPosition(buffer.getCursorPosition().offset(0, -count)); break;
        case 'B': buffer.setSafeCursorPosition(buffer.getCursorPosition().offset(0, count)); break;
        case 'C': buffer.setSafeCursorPosition(buffer.getCursorPosition().offset(count, 0)); break;
        case 'D': buffer.setSafeCursorPosition(buffer.getCursorPosition().offset(-count, 0)); break;
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

    private void processSetCursor(final Buffer<T> buffer, final String sequence) {
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
