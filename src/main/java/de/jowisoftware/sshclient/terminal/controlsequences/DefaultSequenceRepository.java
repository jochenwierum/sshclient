package de.jowisoftware.sshclient.terminal.controlsequences;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

public class DefaultSequenceRepository <T extends GfxChar> implements SequenceRepository<T> {
    private static class WarnSequenceHandler<T extends GfxChar> implements ANSISequence<T> {
        private static final Logger LOGGER = Logger.getLogger(DefaultSequenceRepository.class);

        private final char c;
        public WarnSequenceHandler(final char c) {
            this.c = c;
        }

        @Override
        public void process(final Session<T> sessionInfo,
                final String... args) {
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

    private final List<NonASCIIControlSequence<T>> knownSequences =
        new LinkedList<NonASCIIControlSequence<T>>();

    public void addControlSequence(final NonASCIIControlSequence<T> seq) {
        knownSequences.add(seq);
    }

    @Override
    public LinkedList<NonASCIIControlSequence<T>> getNonASCIISequences() {
        return new LinkedList<NonASCIIControlSequence<T>>(knownSequences);
    }

    @Override
    public ANSISequence<T> getANSISequence(final char c) {
        switch(c) {
        case 'A': return new ANSISequenceABCD<T>(0, -1);
        case 'B': return new ANSISequenceABCD<T>(0, 1);
        case 'C': return new ANSISequenceABCD<T>(1, 0);
        case 'D': return new ANSISequenceABCD<T>(-1, 0);
        case 'G': return new ANSISequenceG<T>();
        case 'H': return new ANSISequenceHf<T>();
        case 'J': return new ANSISequenceJ<T>();
        case 'K': return new ANSISequenceK<T>();
        case 'L': return new ANSISequenceL<T>();
        case 'X': return new ANSISequenceX<T>();
        case 'r': return new ANSISequencer<T>();
        case 'c': return new ANSISequencec<T>();
        case 'd': return new ANSISequenced<T>();
        case 'f': return new ANSISequenceHf<T>();
        case 'h': return new ANSISequenceHighLow<T>(true);
        case 'l': return new ANSISequenceHighLow<T>(false);
        case 'm': return new ANSISequencem<T>();
        default: return new WarnSequenceHandler<T>(c);
        }
    }

    public static <T extends GfxChar> void executeAnsiSequence(
            final char c, final Session<T> sessionInfo, final String... args) {
        new DefaultSequenceRepository<T>().getANSISequence(c).process(sessionInfo, args);
    }
}
