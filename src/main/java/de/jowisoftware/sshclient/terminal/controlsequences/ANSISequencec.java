package de.jowisoftware.sshclient.terminal.controlsequences;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

public class ANSISequencec<T extends GfxChar> implements ANSISequence<T> {
    private static final Logger LOGGER = Logger.getLogger(ANSISequencec.class);
    @Override
    public void process(final Session<T> sessionInfo, final String... args) {
        if (args.length == 0 || args[0].equals("0") || args[0].equals("1")) {
            sessionInfo.sendToServer("\u001b[?6c");
        } else if(args[0].equals(">") || args[0].equals(">0") || args[0].equals(">1")) {
            sessionInfo.sendToServer("\u001b[0;1;0c"); // TODO: return version instead of 1
        } else {
            showWarning(args);
        }
    }
    private void showWarning(final String... args) {
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
