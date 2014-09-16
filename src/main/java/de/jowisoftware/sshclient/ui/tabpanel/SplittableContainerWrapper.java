package de.jowisoftware.sshclient.ui.tabpanel;

import javax.swing.*;

public interface SplittableContainerWrapper {
    JComponent getComponent();

    void setLastFocus(TabbedPaneWrapper tabbedPane, Tab tab);

    void split(SplitDirection direction, TabbedPaneWrapper topLeftComponent,
            TabbedPaneWrapper bottomRightComponent);

    void setParent(SplittableContainerWrapper parent);
    void deleteChild(SplittableContainerWrapper container);
    void merge();

    void updateComponent();

    TabbedPaneWrapper findTabOwner(Tab tab);
}
