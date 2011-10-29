package de.jowisoftware.sshclient.terminal.input.controlsequences;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.Position;

public class ANSISequenceCapitalABCD implements ANSISequence {
    private final int dx;
    private final int dy;

    public ANSISequenceCapitalABCD(final int dx, final int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void process(final SSHSession sessionInfo, final String... args) {
        int count = 1;
        if (args.length == 1 && !args[0].equals("1") && !args[0].equals("0")) {
            count = Integer.parseInt(args[0]);
        }

        final Buffer buffer = sessionInfo.getBuffer();
        final Position newPosition = buffer
                .getCursorPosition().offset(count * dx, count * dy)
                .moveInRange(buffer.getSize().toRange());
        buffer.setCursorPosition(newPosition);
    }
}
