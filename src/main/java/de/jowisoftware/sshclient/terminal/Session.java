package de.jowisoftware.sshclient.terminal;

import de.jowisoftware.sshclient.events.EventHub;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

public interface Session<T extends GfxChar> {
    void sendToServer(String string);
    void sendToServer(byte[] bs);

    Buffer<T> getBuffer();
    EventHub<KeyboardEvent> getKeyboardFeedback();
    EventHub<VisualEvent> getVisualFeedback();
    GfxCharSetup<T> getCharSetup();

    DisplayType getDisplayType();
    void setDisplayType(DisplayType newDisplayType);
}
