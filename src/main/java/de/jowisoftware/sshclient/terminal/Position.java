package de.jowisoftware.sshclient.terminal;

public class Position {
    public final int x;
    public final int y;

    public Position(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public Position offset(final int dx, final int dy) {
        return new Position(x + dx, y + dy);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
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

        final Position other = (Position) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public String toString() {
        return "[Position: " + x + "/" + y + "]";
    }

    public Position withX(final int x) {
        if (x == this.x) {
            return this;
        }
        return new Position(x, this.y);
    }

    public Position withY(final int y) {
        if (y == this.y) {
            return this;
        }
        return new Position(this.x, y);
    }

    public Position moveInRange(final Range range) {
        int y = this.y;
        int x = this.x;

        if (range.bottomRight.x >= x && range.bottomRight.y >= y
                && range.topLeft.x <= x && range.topLeft.y <= y) {
            return this;
        }

        if (range.bottomRight.x < x) {
            x = range.topLeft.x;
            ++y;
        } else if (range.topLeft.x > x) {
            x = range.topLeft.x;
        }

        if (range.bottomRight.y < y) {
            x = range.bottomRight.x;
            y = range.bottomRight.y;
        } else if (range.topLeft.y > y) {
            y = range.topLeft.y;
        }

        return new Position(x, y);
    }
}