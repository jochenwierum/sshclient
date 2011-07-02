package de.jowisoftware.ssh.client.terminal;

public class CursorPosition {
    private final int x;
    private final int y;

    public CursorPosition(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {return x;}
    public int getY() {return y;}

    public CursorPosition offset(final int dx, final int dy) {
        return new CursorPosition(x + dx, y + dy);
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

        final CursorPosition other = (CursorPosition) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public String toString() {
        return "[CursorPosition: " + x + "/" + y + "]";
    }
}