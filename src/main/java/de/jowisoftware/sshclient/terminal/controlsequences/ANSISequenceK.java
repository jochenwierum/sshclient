package de.jowisoftware.sshclient.terminal.controlsequences;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.SessionInfo;
import de.jowisoftware.sshclient.ui.GfxChar;

public class ANSISequenceK<T extends GfxChar> implements ANSISequence<T> {
    private static final Logger LOGGER = Logger.getLogger(ANSISequenceK.class);

    @Override
    public void process(final SessionInfo<T> sessionInfo, final String... args) {
        final int mod = args.length == 0 ? 0 : Integer.parseInt(args[0]);
        switch(mod) {
        case 0: sessionInfo.getBuffer().eraseRestOfLine(); break;
        case 1: sessionInfo.getBuffer().eraseStartOfLine(); break;
        case 2: sessionInfo.getBuffer().eraseLine(); break;
        default: LOGGER.error("Unknown control sequence: <ESC>" + mod + "K");
        }
    }
}
