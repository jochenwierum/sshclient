package de.jowisoftware.sshclient.debug;

public enum PerformanceType {
    REQUEST_TO_RENDER(true, "request-to-render to render"),
    REVEICE_CHAR_TO_RENDER(true, "receive-char to render"),
    SELECT_TO_RENDER(false, "last-cursor-selection to render"),
    BACKGROUND_RENDER(false, "background-render to background-rendered");

    public final String niceName;
    public final boolean firstEventIsMoreImportant;

    PerformanceType(final boolean firstEventIsMoreImportant,
            final String niceName) {
        this.firstEventIsMoreImportant = firstEventIsMoreImportant;
        this.niceName = niceName;
    }
}
