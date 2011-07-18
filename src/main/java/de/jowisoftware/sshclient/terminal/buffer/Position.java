package de.jowisoftware.sshclient.terminal.buffer;

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

    public Position withX(final int newX) {
        if (newX == this.x) {
            return this;
        }
        return new Position(newX, this.y);
    }

    public Position withY(final int newY) {
        if (newY == this.y) {
            return this;
        }
        return new Position(this.x, newY);
    }

    public Position moveInRange(final Range range) {
        int newY = this.y;
        int newX = this.x;

        if (range.bottomRight.x >= newX && range.bottomRight.y >= newY
                && range.topLeft.x <= newX && range.topLeft.y <= newY) {
            return this;
        }

        if (range.bottomRight.x < newX) {
            newX = range.bottomRight.x;
        } else if (range.topLeft.x > newX) {
            newX = range.topLeft.x;
        }

        if (range.bottomRight.y < newY) {
            newY = range.bottomRight.y;
        } else if (range.topLeft.y > newY) {
            newY = range.topLeft.y;
        }

        return new Position(newX, newY);
    }

    public Range toRange() {
        return new Range(this);
    }
}