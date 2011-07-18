package de.jowisoftware.sshclient.terminal;

import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

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
