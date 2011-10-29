package de.jowisoftware.sshclient.terminal.gfx;

public enum Attribute {
    BRIGHT(1, 22), DIM(2, 23), UNDERSCORE(4, 24), BLINK(5, 25), INVERSE(7, 27), HIDDEN(8, 28);

    private int activateSequance;
    private Integer deactivateSequence;

    private Attribute(final int activateSequence, final Integer deactivateSequence) {
        this.activateSequance = activateSequence;
        this.deactivateSequence = deactivateSequence;
    }

    public boolean isActivateSequence(final int seq) {
        return activateSequance == seq;
    }

    public boolean isDeactivateSequence(final int seq) {
        return deactivateSequence != null && deactivateSequence == seq;
    }
}