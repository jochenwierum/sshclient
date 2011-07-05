package de.jowisoftware.sshclient.terminal.controlsequences;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.SessionInfo;
import de.jowisoftware.sshclient.ui.GfxChar;

public class ANSISequencec<T extends GfxChar> implements ANSISequence<T> {
    private static final Logger LOGGER = Logger.getLogger(ANSISequencec.class);
    @Override
    public void process(final SessionInfo<T> sessionInfo, final String... args) {
        if (args.length == 0 || args[0].equals(">")) {
            sessionInfo.respond("\u001b[?1;2c");
        } else {
            final StringBuilder builder = new StringBuilder();
            builder.append("Ignoring unknown arguments: <ESC>[");
            for (int i = 0; i < args.length; ++i) {
                builder.append(args[i]);
                if (i < args.length - 1) {
                    builder.append(";");
                }
            }
            builder.append("c");
            LOGGER.warn(builder.toString());
        }
    }
}
