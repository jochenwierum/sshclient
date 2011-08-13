package de.jowisoftware.sshclient.terminal.controlsequences;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.DisplayType;
import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.BufferSelection;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.buffer.Position;

public class ANSISequenceHighLow<T extends GfxChar> implements ANSISequence<T> {
    private static final Logger LOGGER = Logger.getLogger(ANSISequenceHighLow.class);
    private final boolean isHigh;

    public ANSISequenceHighLow(final boolean isHigh) {
        this.isHigh = isHigh;
    }

    @Override
    public void process(final Session<T> sessionInfo, final String... args) {
        if (args.length > 0 && args[0].startsWith("?")) {
            args[0] = args[0].substring(1);
            processSpecialArgs(sessionInfo, args);
        } else {
            processArgs(sessionInfo, args);
        }
    }

    private void processArgs(final Session<T> sessionInfo, final String[] args) {
        for (int i = 0; i < args.length; ++i) {
            /*
             * 4: smooth-scroll
             */
            LOGGER.warn("High/low flag not implemented: " + args[i]);
        }
    }

    private void processSpecialArgs(final Session<T> sessionInfo,
            final String[] args) {
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("1")) {
                processAppMode(sessionInfo);
            } else if (args[i].equals("3")) {
                processDisplayType(sessionInfo);
            } else if (args[i].equals("6")) {
                processOriginMode(sessionInfo);
            } else if (args[i].equals("7")) {
                processAutoWrap(sessionInfo);
            } else if (args[i].equals("25")) {
                processShowCursor(sessionInfo);
            } else if (args[i].equals("1047")) {
                processAlternateScreen(sessionInfo);
            } else if (args[i].equals("1048")) {
                processCursorStorage(sessionInfo);
            } else if (args[i].equals("1049")) {
                processCursorStorageWithCursorSave(sessionInfo);
            } else {
                LOGGER.warn("High/low flag not implemented: ?" + args[i]);
            }
        }
    }

    private void processAppMode(final Session<T> sessionInfo) {
        sessionInfo.getKeyboardFeedback().setCursorKeysIsAppMode(isHigh);
    }

    private void processShowCursor(final Session<T> sessionInfo) {
        sessionInfo.getBuffer().setShowCursor(isHigh);
    }

    private void processAutoWrap(final Session<T> sessionInfo) {
        sessionInfo.getBuffer().setAutoWrap(isHigh);
    }

    private void processCursorStorageWithCursorSave(final Session<T> sessionInfo) {
        final Buffer<T> buffer = sessionInfo.getBuffer();
        if (isHigh) {
            buffer.switchBuffer(BufferSelection.ALTERNATIVE);
            buffer.saveCursorPosition();
            buffer.erase(buffer.getSize().toRange());
        } else {
            buffer.switchBuffer(BufferSelection.PRIMARY);
            buffer.restoreCursorPosition();
        }
    }

    private void processCursorStorage(final Session<T> sessionInfo) {
        if (isHigh) {
            sessionInfo.getBuffer().saveCursorPosition();
        } else {
            sessionInfo.getBuffer().restoreCursorPosition();
        }
    }

    private void processAlternateScreen(final Session<T> sessionInfo) {
        final Buffer<T> buffer = sessionInfo.getBuffer();
        if (isHigh) {
            buffer.switchBuffer(BufferSelection.ALTERNATIVE);
        } else {
            if (buffer.getSelectedBuffer() == BufferSelection.ALTERNATIVE) {
                buffer.erase(buffer.getSize().toRange());
            }
            buffer.switchBuffer(BufferSelection.PRIMARY);
        }
    }

    private void processOriginMode(final Session<T> sessionInfo) {
        sessionInfo.getBuffer().setCursorRelativeToMargin(isHigh);
        sessionInfo.getBuffer().setCursorPosition(new Position(1, 1));
    }

    private void processDisplayType(final Session<T> sessionInfo) {
        if (isHigh) {
            sessionInfo.getVisualFeedback().setDisplayType(DisplayType.FIXED132X24);
        } else {
            sessionInfo.getVisualFeedback().setDisplayType(DisplayType.FIXED80X24);
        }
        final Buffer<T> buffer = sessionInfo.getBuffer();
        buffer.erase(buffer.getSize().toRange());
        buffer.setCursorPosition(new Position(1, 1));
    }
}
