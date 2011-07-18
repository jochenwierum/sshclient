package de.jowisoftware.sshclient.terminal.controlsequences;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.DisplayType;
import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.util.StringUtils;

public class ANSISequenceHighLow<T extends GfxChar> implements ANSISequence<T> {
    private static final Logger LOGGER = Logger.getLogger(ANSISequenceHighLow.class);
    private final boolean isHigh;

    public ANSISequenceHighLow(final boolean isHigh) {
        this.isHigh = isHigh;
    }

    @Override
    public void process(final Session<T> sessionInfo, final String... args) {
        if (args[0].equals("?1")) {
            sessionInfo.getKeyboardFeedback().setCursorKeysIsAppMode(isHigh);
        } else if (args[0].equals("?3")) {
            processDisplayType(sessionInfo);
        } else if (args[0].equals("?6")) {
            processOriginMode(sessionInfo);
        } else {
            LOGGER.warn("Ignoring unknown high/low flag: " + StringUtils.join(";", args));
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
