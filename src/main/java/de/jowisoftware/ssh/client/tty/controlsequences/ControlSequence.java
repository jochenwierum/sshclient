package de.jowisoftware.ssh.client.tty.controlsequences;

import de.jowisoftware.ssh.client.tty.Buffer;
import de.jowisoftware.ssh.client.tty.GfxCharSetup;
import de.jowisoftware.ssh.client.ui.GfxChar;

public interface ControlSequence<T extends GfxChar> {
    boolean isPartialStart(CharSequence sequence);
    boolean canHandleSequence(CharSequence sequence);
    void handleSequence(String sequence, Buffer<T> buffer, GfxCharSetup<T> setup);
}
