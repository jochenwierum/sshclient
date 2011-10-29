package de.jowisoftware.sshclient.terminal.controlsequences;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;

public class CursorControlSequence implements NonASCIIControlSequence {
    private static final Logger LOGGER = Logger.getLogger(CursorControlSequence.class);
    private static final Pattern PATTERN = Pattern.compile("[DEM]");

    @Override
    public boolean isPartialStart(final String sequence) {
        return false;
    }

    @Override
    public boolean canHandleSequence(final String sequence) {
        return PATTERN.matcher(sequence).matches();
    }

    @Override
    public void handleSequence(final String sequence, final Session sessionInfo) {
        if (sequence.equals("D") || sequence.equals("E") || sequence.endsWith("M")) {
            processRollCursor(sessionInfo.getBuffer(), sequence);
        } else {
            LOGGER.error("Unknown control sequence: <ESC>" + sequence);
        }
    }

    private void processRollCursor(final Buffer buffer, final String sequence) {
        if (sequence.equals("D")) {
            buffer.moveCursorDownAndRoll(false);
        } else if (sequence.endsWith("E")) {
            buffer.moveCursorDownAndRoll(true);
        } else if (sequence.endsWith("M")) {
            buffer.moveCursorUpAndRoll();
        }
    }
}
