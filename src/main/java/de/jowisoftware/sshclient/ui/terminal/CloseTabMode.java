package de.jowisoftware.sshclient.ui.terminal;

public enum CloseTabMode {
    NO_ERROR("when no error occured"),
    NEVER("never"),
    ALWAYS("always");

    public final String niceName;
    CloseTabMode(final String niceName) {
        this.niceName = niceName;
    }
}
