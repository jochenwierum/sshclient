package de.jowisoftware.sshclient.terminal.controlsequences;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.Session;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.Range;

public class ANSISequenceCapitalK implements ANSISequence {
    private static final Logger LOGGER = Logger.getLogger(ANSISequenceCapitalK.class);

    @Override
    public void process(final Session sessionInfo, final String... args) {
        final int mod = args.length == 0 ? 0 : Integer.parseInt(args[0]);

        final Buffer buffer = sessionInfo.getBuffer();
        switch(mod) {
        case 0: buffer.erase(new Range(buffer.getCursorPosition(),
                        buffer.getCursorPosition().withX(buffer.getSize().x))); break;
        case 1: buffer.erase(new Range(buffer.getCursorPosition().withX(1),
                buffer.getCursorPosition())); break;
        case 2: buffer.erase(new Range(buffer.getCursorPosition().withX(1),
                buffer.getCursorPosition().withX(buffer.getSize().x))); break;
        default: LOGGER.error("Unknown control sequence: <ESC>" + mod + "K");
        }
    }
}
