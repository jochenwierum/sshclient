package de.jowisoftware.sshclient.ui.tabpanel;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TabbedPaneWrapper {
    private final List<Tab> tabs = new ArrayList<>();
    private final JTabbedPane pane;
    private SplittableContainerWrapper parentPanel;

    public TabbedPaneWrapper(final TabPanel root) {
        pane = new DnDTabbedPane(this, root);
        pane.addChangeListener(createChangeListener());
    }

    private ChangeListener createChangeListener() {
        return new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                propagateSelection();
            }
        };
    }

    public JComponent getComponent() {
        return pane;
    }

    public void add(final Tab tab) {
        add(tab, tabs.size());
    }

    public void add(final Tab tab, final int index) {
        tabs.add(index, tab);

        final Component component = tab.getContent();
        pane.add(component, index);
        pane.setTabComponentAt(index, tab.getTitleContent());

        pane.setSelectedIndex(index);
        component.requestFocusInWindow();
        propagateSelection();
    }

    public void remove(final Tab tab) {
        final int index = tabs.indexOf(tab);
        tabs.remove(index);
        pane.removeTabAt(index);

        if (tabs.size() == 0) {
            parentPanel.merge();
        }
    }

    public int getTabCount() {
        return pane.getTabCount();
    }

    public void split(final SplitDirection direction, final TabbedPaneWrapper bottomRightComponent) {
        parentPanel.split(direction, this, bottomRightComponent);
    }

    public void setParent(final SplittableContainerWrapper splitContainer) {
        this.parentPanel = splitContainer;
    }

    private void propagateSelection() {
        final Tab tab = getFocusedTab();
        if (tab != null) {
            parentPanel.setLastFocus(TabbedPaneWrapper.this, tab);
        }
    }

    public Tab getFocusedTab() {
        final int index = pane.getSelectedIndex();
        if (index != -1) {
            return tabs.get(index);
        } else {
            return null;
        }
    }

    public boolean hasTab(final Tab tab) {
        for (final Tab possibleResult : tabs) {
            if (tab == possibleResult) {
                return true;
            }
        }
        return false;
    }

    public Tab getTab(final int index) {
        return tabs.get(index);
    }

    public void moveTab(final Tab tab, final int newIndex) {
        final int ti = tabs.indexOf(tab);

        final int pos;
        if (newIndex > ti) {
            pos = newIndex - 1;
        } else {
            pos = newIndex;
        }

        tabs.remove(ti);
        pane.remove(ti);

        add(tab, pos);
        pane.setSelectedIndex(pos);
        propagateSelection();
    }

    public void freezeTabs() {
        for (final Tab tab : tabs) {
            tab.freeze();
        }
    }

    public void unfreezeTabs() {
        for (final Tab tab : tabs) {
            tab.unfreeze();
        }
    }

    public void setFocus(final Tab tab) {
        final int position = tabs.indexOf(tab);
        pane.setSelectedIndex(position);
        propagateSelection();
    }

    public Collection<Tab> getTabs() {
        return new ArrayList<>(tabs);
    }
}
