package de.jowisoftware.sshclient.terminal;

import de.jowisoftware.sshclient.ui.GfxChar;

public interface SessionInfo<T extends GfxChar> {
    Buffer<T> getBuffer();
    KeyboardFeedback getKeyboardFeedback();
    VisualFeedback getVisualFeedback();
    GfxCharSetup<T> getCharSetup();
    void respond(String string);
}
