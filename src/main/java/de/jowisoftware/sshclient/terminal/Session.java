package de.jowisoftware.sshclient.terminal;

import de.jowisoftware.sshclient.ui.GfxChar;

public interface Session<T extends GfxChar> {
    void sendToServer(String string);
    void sendToServer(byte[] bs);

    Buffer<T> getBuffer();
    KeyboardFeedback getKeyboardFeedback();
    VisualFeedback getVisualFeedback();
    GfxCharSetup<T> getCharSetup();

    DisplayType getDisplayType();
    void setDisplayType(DisplayType newDisplayType);
}
