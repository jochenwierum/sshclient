package de.jowisoftware.sshclient.ui.tabpanel;

import javax.swing.JSplitPane;

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
