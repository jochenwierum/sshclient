package de.jowisoftware.ssh.client.terminal;

public enum Color {
    DEFAULT(null, null), DEFAULTBG(null, null),
    BLACK(30, 40), RED(31, 41), GREEN(32, 42), YELLOW(33, 43), BLUE(34, 44),
    MAGENTA(35, 45), CYAN(36, 46), WHITE(37, 47);

    private Integer fgSequence;
    private Integer bgSequence;
    private Color(final Integer fgSequence, final Integer bgSequence) {
        this.fgSequence = fgSequence;
        this.bgSequence = bgSequence;
    }

    public boolean isForegroundSequence(final int seq) {
        return fgSequence != null && fgSequence == seq;
    }

    public boolean isBackgroundSequence(final int seq) {
        return bgSequence != null && bgSequence == seq;
    }
}