package de.jowisoftware.ssh.client.terminal.controlsequences;

import de.jowisoftware.ssh.client.terminal.Buffer;
import de.jowisoftware.ssh.client.terminal.GfxCharSetup;
import de.jowisoftware.ssh.client.terminal.KeyboardFeedback;
import de.jowisoftware.ssh.client.ui.GfxChar;

public interface ControlSequence<T extends GfxChar> {
    boolean isPartialStart(CharSequence sequence);
    boolean canHandleSequence(CharSequence sequence);
    void handleSequence(String sequence, Buffer<T> buffer, GfxCharSetup<T> setup, KeyboardFeedback keyboardFeedback);
}
