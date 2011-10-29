package de.jowisoftware.sshclient.terminal;

import de.jowisoftware.sshclient.events.EventHub;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;

public interface Session {
    void sendToServer(String string);
    void sendToServer(byte[] bs);

    Buffer getBuffer();
    EventHub<KeyboardEvent> getKeyboardFeedback();
    EventHub<VisualEvent> getVisualFeedback();
    GfxCharSetup getCharSetup();

    DisplayType getDisplayType();
    void setDisplayType(DisplayType newDisplayType);
}
