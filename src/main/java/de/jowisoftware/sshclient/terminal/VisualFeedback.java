package de.jowisoftware.sshclient.terminal;

public interface VisualFeedback {
    void bell();
    void setTitle(String title);
    void setDisplayType(DisplayType displayType);
}
