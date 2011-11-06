package de.jowisoftware.sshclient.terminal.mouse;

import java.io.Serializable;

public interface ClipboardManager extends Serializable {
    void copyPlaintext(String string);
}
