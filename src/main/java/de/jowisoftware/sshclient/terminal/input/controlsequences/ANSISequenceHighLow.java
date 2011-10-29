package de.jowisoftware.sshclient.terminal.input.controlsequences;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.BufferSelection;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.events.DisplayType;
import de.jowisoftware.sshclient.terminal.gfx.GfxCharSetup;

public class ANSISequenceHighLow implements ANSISequence {
    private static final Logger LOGGER = Logger.getLogger(ANSISequenceHighLow.class);
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
            processArgs(sessionInfo, args);
        }
    }

    private void processArgs(final SSHSession sessionInfo, final String[] args) {
        for (int i = 0; i < args.length; ++i) {
            // 4: insert mode
            LOGGER.warn("High/low flag not implemented: " + args[i]);
        }
    }

    private void processSpecialArgs(final SSHSession sessionInfo,
            final String[] args) {
        for (int i = 0; i < args.length; ++i) {
            /*
             * 4: smooth-scroll
             * 5: Reverse Video (DECSCNM) (swap default fore- and background)
             * 8: DECARM: don't auto-repeat keypresses
             * 12:
             * 1000: Send mouse clicks
             */

            if (args[i].equals("1")) {
                processAppMode(sessionInfo);
            } else if (args[i].equals("3")) {
                processDisplayType(sessionInfo);
            } else if (args[i].equals("5")) {
                processReverseVideo(sessionInfo);
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

    private void processReverseVideo(final SSHSession sessionInfo) {
        final GfxCharSetup charSetup = sessionInfo.getCharSetup();
        charSetup.setInverseMode(isHigh);
        sessionInfo.getBuffer().setClearChar(charSetup.createClearChar());
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
            buffer.switchBuffer(BufferSelection.ALTERNATIVE);
            buffer.saveCursorPosition();
            buffer.erase(buffer.getSize().toRange());
        } else {
            buffer.switchBuffer(BufferSelection.PRIMARY);
            buffer.restoreCursorPosition();
        }
    }

    private void processCursorStorage(final SSHSession sessionInfo) {
        if (isHigh) {
            sessionInfo.getBuffer().saveCursorPosition();
        } else {
            sessionInfo.getBuffer().restoreCursorPosition();
        }
    }

    private void processAlternateScreen(final SSHSession sessionInfo) {
        final Buffer buffer = sessionInfo.getBuffer();
        if (isHigh) {
            buffer.switchBuffer(BufferSelection.ALTERNATIVE);
        } else {
            if (buffer.getSelectedBuffer() == BufferSelection.ALTERNATIVE) {
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
