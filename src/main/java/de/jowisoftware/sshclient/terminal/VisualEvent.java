package de.jowisoftware.sshclient.terminal;

public interface VisualEvent {
    void bell();
    void newTitle(String title);
    void setDisplayType(DisplayType displayType);
}
