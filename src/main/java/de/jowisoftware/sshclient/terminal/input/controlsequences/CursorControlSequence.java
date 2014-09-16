package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class CursorControlSequence implements NonASCIIControlSequence {
    private static final Logger LOGGER = LoggerFactory.getLogger(CursorControlSequence.class);
    private static final Pattern PATTERN = Pattern.compile("[DEM78]");

    @Override
    public boolean isPartialStart(final String sequence) {
        return false;
    }

    @Override
    public boolean canHandleSequence(final String sequence) {
        return PATTERN.matcher(sequence).matches();
    }

    @Override
    public void handleSequence(final String sequence, final SSHSession sessionInfo) {
        if (sequence.equals("D") || sequence.equals("E") || sequence.endsWith("M")) {
            processRollCursor(sessionInfo.getBuffer(), sequence);
        } else if (sequence.equals("7") || sequence.equals("8")) {
            processCursorPositionManagerment(sessionInfo, sequence);
        } else {
            LOGGER.error("Unknown control sequence: <ESC>{}", sequence);
        }
    }

    private void processCursorPositionManagerment(final SSHSession sessionInfo, final String sequence) {
        if (sequence.endsWith("7")) {
            sessionInfo.saveState();
        } else if (sequence.endsWith("8")) {
            sessionInfo.restoreState();
        }
    }

    private void processRollCursor(final Buffer buffer, final String sequence) {
        if (sequence.equals("D")) {
            buffer.moveCursorDown(false);
        } else if (sequence.endsWith("E")) {
            buffer.moveCursorDown(true);
        } else if (sequence.endsWith("M")) {
            buffer.moveCursorUp();
        }
    }
}
