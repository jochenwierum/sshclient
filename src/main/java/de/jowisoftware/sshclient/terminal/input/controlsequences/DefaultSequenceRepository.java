package de.jowisoftware.sshclient.terminal.input.controlsequences;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

public class DefaultSequenceRepository implements SequenceRepository {
    private static class WarnSequenceHandler implements ANSISequence {
        private static final Logger LOGGER = Logger.getLogger(DefaultSequenceRepository.class);

        private final char c;
        public WarnSequenceHandler(final char c) {
            this.c = c;
        }

        @Override
        public void process(final SSHSession sessionInfo,
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

    private final List<NonASCIIControlSequence> knownSequences =
        new LinkedList<NonASCIIControlSequence>();

    public DefaultSequenceRepository() {
        knownSequences.add(new CursorControlSequence());
        knownSequences.add(new KeyboardControlSequence());
        knownSequences.add(new OperatingSystemCommandSequence());
        knownSequences.add(new DebugControlSequence());
        knownSequences.add(new CharsetControlSequence());
        knownSequences.add(new ColorCommandSequence());
    }

    @Override
    public List<NonASCIIControlSequence> getNonASCIISequences() {
        return new LinkedList<NonASCIIControlSequence>(knownSequences);
    }

    @Override
    public ANSISequence getANSISequence(final char c) {
        switch(c) {
        case 'A': return new ANSISequenceCapitalABCD(0, -1);
        case 'B': return new ANSISequenceCapitalABCD(0, 1);
        case 'C': return new ANSISequenceCapitalABCD(1, 0);
        case 'D': return new ANSISequenceCapitalABCD(-1, 0);
        case 'G': return new ANSISequenceCapitalG();
        case 'H': return new ANSISequenceCapitalHf();
        case 'J': return new ANSISequenceCapitalJ();
        case 'K': return new ANSISequenceCapitalK();
        case 'L': return new ANSISequenceCapitalL();
        case 'P': return new ANSISequenceCapitalP();
        case 'X': return new ANSISequenceCapitalX();
        case 'r': return new ANSISequencer();
        case 'c': return new ANSISequencec();
        case 'd': return new ANSISequenced();
        case 'f': return new ANSISequenceCapitalHf();
        case 'h': return new ANSISequenceHighLow(true);
        case 'l': return new ANSISequenceHighLow(false);
        case 'm': return new ANSISequencem();
        case '@': return new ANSISequenceAt();
        default: return new WarnSequenceHandler(c);
        }
    }

    public static <T extends GfxChar> void executeAnsiSequence(
            final char c, final SSHSession sessionInfo, final String... args) {
        new DefaultSequenceRepository().getANSISequence(c).process(sessionInfo, args);
    }
}
