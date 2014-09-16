package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ANSISequenceCapitalJ implements ANSISequence {
    private static final Logger LOGGER = LoggerFactory.getLogger(ANSISequenceCapitalJ.class);

    @Override
    public void process(final SSHSession sessionInfo, final String... args) {
        final int mod = args.length == 0 ? 0 : Integer.parseInt(args[0]);
        final Buffer buffer = sessionInfo.getBuffer();

        switch(mod) {
        case 0:
            buffer.erase(new Range(buffer.getCursorPosition(),
                buffer.getSize()));
            break;
        case 1:
            buffer.erase(buffer.getCursorPosition().toRange());
            break;
        case 2:
            buffer.erase(buffer.getSize().toRange());
            break;
        default: LOGGER.error("Unknown control sequence: <ESC> {}J", mod);
        }
    }
}
