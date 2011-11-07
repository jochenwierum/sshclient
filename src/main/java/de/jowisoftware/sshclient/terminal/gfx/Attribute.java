package de.jowisoftware.sshclient.terminal.gfx;

public enum Attribute {
    BRIGHT(1, 22, 1), DIM(2, 23, 2), UNDERSCORE(4, 24, 4),
    BLINK(5, 25, 8), INVERSE(7, 27, 16), HIDDEN(8, 28, 32);

    public final int flag;
    private int activateSequance;
    private int deactivateSequence;

    private Attribute(final int activateSequence,
            final int deactivateSequence,
            final int flag) {
        this.activateSequance = activateSequence;
        this.deactivateSequence = deactivateSequence;
        this.flag = flag;
    }

    public boolean isActivateSequence(final int seq) {
        return activateSequance == seq;
    }

    public boolean isDeactivateSequence(final int seq) {
        return deactivateSequence == seq;
    }
}