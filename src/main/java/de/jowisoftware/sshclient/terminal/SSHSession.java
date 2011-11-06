package de.jowisoftware.sshclient.terminal;

import de.jowisoftware.sshclient.events.EventHub;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.Renderer;
import de.jowisoftware.sshclient.terminal.buffer.TabStopManager;
import de.jowisoftware.sshclient.terminal.events.DisplayType;
import de.jowisoftware.sshclient.terminal.events.KeyboardEvent;
import de.jowisoftware.sshclient.terminal.events.VisualEvent;
import de.jowisoftware.sshclient.terminal.gfx.GfxCharSetup;

public interface SSHSession {
    void sendToServer(String string);
    void sendToServer(byte[] bs);

    Buffer getBuffer();
    EventHub<KeyboardEvent> getKeyboardFeedback();
    EventHub<VisualEvent> getVisualFeedback();
    GfxCharSetup getCharSetup();
    TabStopManager getTabStopManager();
    Renderer getRenderer();

    DisplayType getDisplayType();
    void setDisplayType(DisplayType newDisplayType);
}
