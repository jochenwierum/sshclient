package de.jowisoftware.sshclient.ui.tabpanel;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class RootContainerWrapper implements SplittableContainerWrapper {
    private final JPanel panel = new JPanel();
    private final TabPanel parent;
    private final SimpleSplittableContainerWrapper root;

    public RootContainerWrapper(final TabPanel parent, final TabbedPaneWrapper firstPane) {
        this.parent = parent;

        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder());

        root = new SimpleSplittableContainerWrapper(this, firstPane.getComponent());
        firstPane.setParent(root);

        updateComponent();
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

    @Override
    public void setLastFocus(final TabbedPaneWrapper tabbedPane, final Tab tab) {
        parent.setLastFocus(tabbedPane, tab);
    }

    @Override
    public void updateComponent() {
        panel.removeAll();
        panel.add(root.getComponent(), BorderLayout.CENTER);
        panel.validate();
    }

    @Override
    public void deleteChild(final SplittableContainerWrapper componentToDelete) {
        updateComponent();
    }

    @Override
    public void split(final SplitDirection direction, final TabbedPaneWrapper topLeftComponent,
            final TabbedPaneWrapper bottomRightComponent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setParent(final SplittableContainerWrapper parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void merge() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TabbedPaneWrapper findTabOwner(final Tab tab) {
        return root.findTabOwner(tab);
    }

    public void lock() {
        final HighlightingGlassPane glasspane = new HighlightingGlassPane(this);
        root.getComponent().getRootPane().setGlassPane(glasspane);
        glasspane.setVisible(true);
    }

    public void unlock() {
        root.getComponent().getRootPane().getGlassPane().setVisible(false);
    }

    public TabbedPaneWrapper[] findTabPanes() {
        final List<TabbedPaneWrapper> panes = new ArrayList<>();
        root.findTabPanes(panes);
        return panes.toArray(new TabbedPaneWrapper[panes.size()]);
    }
}
