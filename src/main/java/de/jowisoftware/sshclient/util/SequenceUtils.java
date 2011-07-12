package de.jowisoftware.sshclient.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.Session;
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
import de.jowisoftware.sshclient.ui.GfxChar;

public final class SequenceUtils {
    private static Map<Character, ANSISequence<? extends GfxChar>> sequenceHandlers = null;
    private static final Object SYNC_OBJ = new Object();

    private SequenceUtils() {
        /* Util classes are not instanciated */
    }

    private static <T extends GfxChar> void initMap() {
        sequenceHandlers = new HashMap<Character, ANSISequence<?>>();
        sequenceHandlers.put('A', new ANSISequenceABCD<T>(0, -1));
        sequenceHandlers.put('B', new ANSISequenceABCD<T>(0, 1));
        sequenceHandlers.put('C', new ANSISequenceABCD<T>(1, 0));
        sequenceHandlers.put('D', new ANSISequenceABCD<T>(-1, 0));
        final ANSISequenceHf<T> sequencesHf = new ANSISequenceHf<T>();
        sequenceHandlers.put('H', sequencesHf);
        sequenceHandlers.put('J', new ANSISequenceJ<T>());
        sequenceHandlers.put('K', new ANSISequenceK<T>());
        sequenceHandlers.put('L', new ANSISequenceL<T>());
        sequenceHandlers.put('r', new ANSISequencer<T>());
        sequenceHandlers.put('c', new ANSISequencec<T>());
        sequenceHandlers.put('f', sequencesHf);
        sequenceHandlers.put('h', new ANSISequenceHighLow<T>(true));
        sequenceHandlers.put('l', new ANSISequenceHighLow<T>(false));
        sequenceHandlers.put('m', new ANSISequencem<T>());
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

    @SuppressWarnings("unchecked")
    public static <T extends GfxChar> ANSISequence<T> getANSISequence(final char c) {
        if (sequenceHandlers == null) {
            synchronized(SYNC_OBJ) {
                if (sequenceHandlers == null) {
                    initMap();
                }
            }
        }

        return sequenceHandlers.containsKey(c) ?
                (ANSISequence<T>) sequenceHandlers.get(c) : new WarnSequenceHandler<T>(c);
    }

    @SuppressWarnings("unchecked")
    public static <T extends GfxChar> void executeAnsiSequence(
            final char c, final Session<T> sessionInfo, final String... args) {
        getANSISequence(c).process((Session<GfxChar>) sessionInfo, args);
    }
}
