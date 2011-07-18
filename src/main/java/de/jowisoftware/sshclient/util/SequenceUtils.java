package de.jowisoftware.sshclient.util;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.controlsequences.ANSISequence;
import de.jowisoftware.sshclient.terminal.controlsequences.ANSISequenceABCD;
import de.jowisoftware.sshclient.terminal.controlsequences.ANSISequenceHf;
import de.jowisoftware.sshclient.terminal.controlsequences.ANSISequenceHighLow;
import de.jowisoftware.sshclient.terminal.controlsequences.ANSISequenceJ;
import de.jowisoftware.sshclient.terminal.controlsequences.ANSISequenceK;
import de.jowisoftware.sshclient.terminal.controlsequences.ANSISequenceL;
import de.jowisoftware.sshclient.terminal.controlsequences.ANSISequencec;
import de.jowisoftware.sshclient.terminal.controlsequences.ANSISequencem;
import de.jowisoftware.sshclient.terminal.controlsequences.ANSISequencer;

public final class SequenceUtils {
    private SequenceUtils() {
        /* Util classes are not instanciated */
    }

    private static class WarnSequenceHandler<T extends GfxChar> implements ANSISequence<T> {
        private static final Logger LOGGER = Logger.getLogger(SequenceUtils.class);

        private final char c;
        public WarnSequenceHandler(final char c) {
            this.c = c;
        }

        @Override
        public void process(final Session<T> sessionInfo, final String... args) {
            final StringBuilder builder = new StringBuilder();
            builder.append("Ignoring unknown ANSI Sequence: <ESC>[");
            for (int i = 0; i < args.length; ++i) {
                builder.append(args[i]);
                if (i < args.length - 1) {
                    builder.append(";");
                }
            }
            builder.append(c);
            LOGGER.warn(builder.toString());
        }
    }

    public static <T extends GfxChar> ANSISequence<T> getANSISequence(final char c) {
        switch(c) {
        case 'A': return new ANSISequenceABCD<T>(0, -1);
        case 'B': return new ANSISequenceABCD<T>(0, 1);
        case 'C': return new ANSISequenceABCD<T>(1, 0);
        case 'D': return new ANSISequenceABCD<T>(-1, 0);
        case 'H': return new ANSISequenceHf<T>();
        case 'J': return new ANSISequenceJ<T>();
        case 'K': return new ANSISequenceK<T>();
        case 'L': return new ANSISequenceL<T>();
        case 'r': return new ANSISequencer<T>();
        case 'c': return new ANSISequencec<T>();
        case 'f': return new ANSISequenceHf<T>();
        case 'h': return new ANSISequenceHighLow<T>(true);
        case 'l': return new ANSISequenceHighLow<T>(false);
        case 'm': return new ANSISequencem<T>();
        default: return new WarnSequenceHandler<T>(c);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends GfxChar> void executeAnsiSequence(
            final char c, final Session<T> sessionInfo, final String... args) {
        getANSISequence(c).process((Session<GfxChar>) sessionInfo, args);
    }
}
