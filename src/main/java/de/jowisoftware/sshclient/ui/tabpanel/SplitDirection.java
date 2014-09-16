package de.jowisoftware.sshclient.ui.tabpanel;

import javax.swing.*;

public enum SplitDirection {
    HORIZONTAL(JSplitPane.HORIZONTAL_SPLIT),
    VERTICAL(JSplitPane.VERTICAL_SPLIT);

    private final int splitPaneFlag;

    private SplitDirection(final int splitPaneFlag) {
        this.splitPaneFlag = splitPaneFlag;
    }

    public int getSplitPaneFlag() {
        return splitPaneFlag;
    }
}
