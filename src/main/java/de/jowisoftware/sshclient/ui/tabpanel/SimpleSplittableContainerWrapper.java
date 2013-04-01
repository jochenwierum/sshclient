package de.jowisoftware.sshclient.ui.tabpanel;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JSplitPane;


public class SimpleSplittableContainerWrapper implements SplittableContainerWrapper {
    private SplittableContainerWrapper parent;
    private SimpleSplittableContainerWrapper leftContainer;
    private SimpleSplittableContainerWrapper rightContainer;
    private JSplitPane splitPane;
    private JComponent component;

    public SimpleSplittableContainerWrapper(final SplittableContainerWrapper parent,
            final JComponent initialComponent) {
        this.parent = parent;
        component = initialComponent;
    }

    protected void setComponent(final JComponent component) {
        this.component = component;
    }

    @Override
    public JComponent getComponent() {
        return component;
    }

    @Override
    public void split(final SplitDirection direction, final TabbedPaneWrapper topLeftComponent,
            final TabbedPaneWrapper bottomRightComponent) {
        //noinspection MagicConstant
        splitPane = new JSplitPane(direction.getSplitPaneFlag());
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        leftContainer = new SimpleSplittableContainerWrapper(this, topLeftComponent.getComponent());
        rightContainer = new SimpleSplittableContainerWrapper(this, bottomRightComponent.getComponent());

        splitPane.setLeftComponent(topLeftComponent.getComponent());
        splitPane.setRightComponent(bottomRightComponent.getComponent());

        topLeftComponent.setParent(leftContainer);
        bottomRightComponent.setParent(rightContainer);

        registerNewComponent(splitPane);
        splitPane.setDividerLocation(.5);
    }

    private void registerNewComponent(final JComponent component) {
        this.component = component;
        parent.updateComponent();
    }

    @Override
    public void updateComponent() {
        final int dividerLocation = splitPane.getDividerLocation();
        splitPane.setLeftComponent(leftContainer.getComponent());
        splitPane.setRightComponent(rightContainer.getComponent());
        splitPane.setDividerLocation(dividerLocation);
        splitPane.doLayout();
    }

    @Override
    public void setLastFocus(final TabbedPaneWrapper tabbedPane, final Tab tab) {
        parent.setLastFocus(tabbedPane, tab);
    }

    @Override
    public void deleteChild(final SplittableContainerWrapper componentToDelete) {
        final SimpleSplittableContainerWrapper survivingComponent;
        if (componentToDelete == leftContainer) {
            survivingComponent = rightContainer;
        } else {
            survivingComponent = leftContainer;
        }

        migrateFrom(survivingComponent);
    }

    @Override
    public void setParent(final SplittableContainerWrapper parent) {
        this.parent = parent;
    }

    @Override
    public void merge() {
        parent.deleteChild(this);
    }

    private void migrateFrom(final SimpleSplittableContainerWrapper oldChild) {
        leftContainer = oldChild.leftContainer;
        rightContainer = oldChild.rightContainer;
        splitPane = oldChild.splitPane;

        if (splitPane != null) {
            leftContainer.setParent(this);
            rightContainer.setParent(this);
        } else {
            ((DnDTabbedPane) oldChild.component).getTabbedPane().setParent(this);
        }

        registerNewComponent(oldChild.component);
    }

    @Override
    public TabbedPaneWrapper findTabOwner(final Tab tab) {
        if (splitPane != null) {
            final TabbedPaneWrapper result = leftContainer.findTabOwner(tab);
            if (result != null) {
                return result;
            } else {
                return rightContainer.findTabOwner(tab);
            }
        } else {
            final TabbedPaneWrapper result = ((DnDTabbedPane) component).getTabbedPane();
            if (result.hasTab(tab)) {
                return result;
            } else {
                return null;
            }
        }
    }

    public void findTabPanes(final List<TabbedPaneWrapper> panes) {
        if (splitPane != null) {
            leftContainer.findTabPanes(panes);
            rightContainer.findTabPanes(panes);
        } else {
            panes.add(((DnDTabbedPane) component).getTabbedPane());
        }
    }
}
