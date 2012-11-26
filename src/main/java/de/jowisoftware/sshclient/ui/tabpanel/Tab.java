package de.jowisoftware.sshclient.ui.tabpanel;

import javax.swing.JComponent;

public interface Tab {
    JComponent getContent();
    JComponent getTitleContent();

    void freeze();
    void unfreeze();
}
