package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.Buffer;
import de.jowisoftware.sshclient.terminal.GfxCharSetup;
import de.jowisoftware.sshclient.terminal.KeyboardFeedback;
import de.jowisoftware.sshclient.ui.GfxChar;

public interface ControlSequence<T extends GfxChar> {
    boolean isPartialStart(CharSequence sequence);
    boolean canHandleSequence(CharSequence sequence);
    void handleSequence(String sequence, Buffer<T> buffer, GfxCharSetup<T> setup, KeyboardFeedback keyboardFeedback);
}
