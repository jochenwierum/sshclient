package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.BufferSelection;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.events.DisplayType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ANSISequenceHighLow implements ANSISequence {
    private static final Logger LOGGER = LoggerFactory.getLogger(ANSISequenceHighLow.class);
    private final boolean isHigh;

    public ANSISequenceHighLow(final boolean isHigh) {
        this.isHigh = isHigh;
    }

    @Override
    public void process(final SSHSession sessionInfo, final String... args) {
        if (args.length > 0 && args[0].startsWith("?")) {
            args[0] = args[0].substring(1);
            processSpecialArgs(sessionInfo, args);
        } else {
            processArgs(args);
        }
    }

    private void processArgs(final String[] args) {
        for (final String arg : args) {
            // 4: insert mode
            LOGGER.warn("High/low flag not implemented: {}", arg);
        }
    }

    private void processSpecialArgs(final SSHSession sessionInfo,
            final String[] args) {
        for (final String arg : args) {
            /*
             * 4: smooth-scroll
             * 5: Reverse Video (DECSCNM) (swap default fore- and background)
             * 8: DECARM: don't auto-repeat keypresses
             * 12:
             * 1000: Send mouse clicks
             */

            switch (arg) {
                case "1":
                    processAppMode(sessionInfo);
                    break;
                case "3":
                    processDisplayType(sessionInfo);
                    break;
                case "5":
                    processReverseVideo(sessionInfo);
                    break;
                case "6":
                    processOriginMode(sessionInfo);
                    break;
                case "7":
                    processAutoWrap(sessionInfo);
                    break;
                case "25":
                    processShowCursor(sessionInfo);
                    break;
                case "1047":
                    processAlternateScreen(sessionInfo);
                    break;
                case "1048":
                    processCursorStorage(sessionInfo);
                    break;
                case "1049":
                    processCursorStorageWithCursorSave(sessionInfo);
                    break;
                default:
                    LOGGER.warn("High/low flag not implemented: ?{}", arg);
            }
        }
    }

    private void processReverseVideo(final SSHSession sessionInfo) {
        sessionInfo.getVisualFeedback().fire().newInverseMode(isHigh);
    }

    private void processAppMode(final SSHSession sessionInfo) {
        sessionInfo.getKeyboardFeedback().fire().newCursorKeysIsAppMode(isHigh);
    }

    private void processShowCursor(final SSHSession sessionInfo) {
        sessionInfo.getBuffer().setShowCursor(isHigh);
    }

    private void processAutoWrap(final SSHSession sessionInfo) {
        sessionInfo.getBuffer().setAutoWrap(isHigh);
    }

    private void processCursorStorageWithCursorSave(final SSHSession sessionInfo) {
        final Buffer buffer = sessionInfo.getBuffer();
        if (isHigh) {
            buffer.switchBuffer(BufferSelection.ALTERNATE);
            sessionInfo.saveState();
            buffer.erase(buffer.getSize().toRange());
        } else {
            buffer.switchBuffer(BufferSelection.PRIMARY);
            sessionInfo.restoreState();
        }
    }

    private void processCursorStorage(final SSHSession sessionInfo) {
        if (isHigh) {
            sessionInfo.saveState();
        } else {
            sessionInfo.restoreState();
        }
    }

    private void processAlternateScreen(final SSHSession sessionInfo) {
        final Buffer buffer = sessionInfo.getBuffer();
        if (isHigh) {
            buffer.switchBuffer(BufferSelection.ALTERNATE);
        } else {
            if (buffer.getSelectedBuffer() == BufferSelection.ALTERNATE) {
                buffer.erase(buffer.getSize().toRange());
            }
            buffer.switchBuffer(BufferSelection.PRIMARY);
        }
    }

    private void processOriginMode(final SSHSession sessionInfo) {
        sessionInfo.getBuffer().setCursorRelativeToMargin(isHigh);
        sessionInfo.getBuffer().setCursorPosition(new Position(1, 1));
    }

    private void processDisplayType(final SSHSession sessionInfo) {
        if (isHigh) {
            sessionInfo.getVisualFeedback().fire().setDisplayType(DisplayType.FIXED132X24);
        } else {
            sessionInfo.getVisualFeedback().fire().setDisplayType(DisplayType.FIXED80X24);
        }
        final Buffer buffer = sessionInfo.getBuffer();
        buffer.erase(buffer.getSize().toRange());
        buffer.setCursorPosition(new Position(1, 1));
    }
}
