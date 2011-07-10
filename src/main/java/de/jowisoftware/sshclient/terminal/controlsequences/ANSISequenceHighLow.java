package de.jowisoftware.sshclient.terminal.controlsequences;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.DisplayType;
import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.ui.GfxChar;
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
        } else if(args[0].equals("?3")) {
            if (isHigh) {
                sessionInfo.getVisualFeedback().setDisplayType(DisplayType.FIXED132X24);
            } else {
                sessionInfo.getVisualFeedback().setDisplayType(DisplayType.FIXED80X24);
            }
        } else {
            LOGGER.warn("Ignoring unknown high/low flag: " + StringUtils.join(";", args));
        }
    }
}
