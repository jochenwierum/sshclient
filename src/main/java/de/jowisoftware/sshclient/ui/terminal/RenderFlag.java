package de.jowisoftware.sshclient.ui.terminal;

public enum RenderFlag {
    SELECTED(1), CURSOR(2), INVERTED(4);

    public final int flag;
    RenderFlag(final int flag) {
        this.flag = flag;
    }
}
