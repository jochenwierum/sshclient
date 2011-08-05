package de.jowisoftware.sshclient.terminal;

public enum TerminalColor {
    DEFAULT(38, null, "default foreground"),
    DEFAULTBG(null, 48, "default background"),
    BLACK(30, 40), RED(31, 41), GREEN(32, 42),
    YELLOW(33, 43), BLUE(34, 44), MAGENTA(35, 45),
    CYAN(36, 46), WHITE(37, 47);

    private Integer fgSequence;
    private Integer bgSequence;
    private String nicename;

    private TerminalColor(final Integer fgSequence, final Integer bgSequence, final String niceName) {
        this.fgSequence = fgSequence;
        this.bgSequence = bgSequence;
        this.nicename = niceName;
    }

    private TerminalColor(final Integer fgSequence, final Integer bgSequence) {
        this.fgSequence = fgSequence;
        this.bgSequence = bgSequence;
        nicename = name();
    }

    public boolean isForegroundSequence(final int seq) {
        return fgSequence != null && fgSequence == seq;
    }

    public boolean isBackgroundSequence(final int seq) {
        return bgSequence != null && bgSequence == seq;
    }

    public String nicename() {
        return nicename;
    }
}