package de.jowisoftware.sshclient.terminal.controlsequences;

import de.jowisoftware.sshclient.terminal.SessionInfo;
import de.jowisoftware.sshclient.ui.GfxChar;

public interface ControlSequence<T extends GfxChar> {
    boolean isPartialStart(CharSequence sequence);
    boolean canHandleSequence(CharSequence sequence);
    void handleSequence(String sequence, SessionInfo<T> sessionInfo);
}
