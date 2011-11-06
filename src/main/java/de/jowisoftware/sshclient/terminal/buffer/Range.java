package de.jowisoftware.sshclient.terminal.buffer;

import java.io.Serializable;

public class Range implements Serializable {
    private static final long serialVersionUID = -1600405466057831973L;

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

    @Override
    public String toString() {
        return "[Range " + topLeft + " - " + bottomRight + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((bottomRight == null) ? 0 : bottomRight.hashCode());
        result = prime * result + ((topLeft == null) ? 0 : topLeft.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Range other = (Range) obj;
        return bottomRight.equals(other.bottomRight) &&  topLeft.equals(other.topLeft);
    }

    public int width() {
        return bottomRight.x - topLeft.x + 1;
    }

    public int height() {
        return bottomRight.y - topLeft.y + 1;
    }

    public Range offset(final int dx, final int dy) {
        return new Range(topLeft.offset(dx, dy), bottomRight.offset(dx, dy));
    }
}
