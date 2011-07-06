package de.jowisoftware.sshclient.terminal;

public class Range {
    public final Position topLeft;
    public final Position bottomRight;

    public Range(final Position position) {
        topLeft = new Position(1, 1);
        bottomRight = position;
    }

    public Range(final Position position, final Position position2) {
        topLeft = new Position(
                Math.min(position.x, position2.x),
                Math.min(position.y, position2.y));
        bottomRight = new Position(
                Math.max(position.x, position2.x),
                Math.max(position.y, position2.y));
    }
}
