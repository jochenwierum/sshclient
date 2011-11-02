package de.jowisoftware.sshclient.terminal.events;

public interface VisualEvent {
    void bell();
    void newTitle(String title);
    void setDisplayType(DisplayType displayType);
    void newInverseMode(boolean active);
}
