package de.jowisoftware.sshclient.ui.tabpanel;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;


public class TabPanel {
    private final RootContainerWrapper root;
    private TabbedPaneWrapper lastFocusedTabPane;
    private Tab lastFocusedTab;
    private final List<TabPanelListener> listeners = new ArrayList<>();

    private KeyListener keyListener;

    public TabPanel() {
        lastFocusedTabPane = new TabbedPaneWrapper(this);
        root = new RootContainerWrapper(this, lastFocusedTabPane);

        setupFocusListener();
    }

    RootContainerWrapper getRoot() {
        return root;
    }

    public JComponent getComponent() {
        return root.getComponent();
    }

    public void add(final Tab tab) {
        lastFocusedTabPane.add(tab);
    }

    void setLastFocus(final TabbedPaneWrapper tabbedPane, final Tab tab) {
        final Tab tmpLastFocusTab = lastFocusedTab;
        lastFocusedTabPane = tabbedPane;
        lastFocusedTab = tab;

        if (tmpLastFocusTab != lastFocusedTab) {
            notifyTabChange();
        }
    }

    private void notifyTabChange() {
        for (final TabPanelListener listener : listeners) {
            listener.selectionChanged(lastFocusedTab);
        }
    }

    public void split(final SplitDirection direction) {
        if (lastFocusedTabPane.getTabCount() > 1) {
            final TabbedPaneWrapper bottomRightComponent = new TabbedPaneWrapper(this);
            bottomRightComponent.getComponent().addKeyListener(keyListener);
            lastFocusedTabPane.split(direction, bottomRightComponent);

            final Tab oldTab = lastFocusedTab;
            lastFocusedTabPane.remove(oldTab);
            bottomRightComponent.add(oldTab);
        }
    }

    public void closeActiveTab() {
        if (lastFocusedTab != null) {
            lastFocusedTabPane.remove(lastFocusedTab);
            setValidFocus();
        }
    }

    public void closeTab(final Tab tab) {
        final TabbedPaneWrapper pane = root.findTabOwner(tab);
        if (pane != null) {
            pane.remove(tab);
            setValidFocus();
        }
    }

    private void setValidFocus() {
        final Tab tmpLastFocusTab = lastFocusedTab;

        if (lastFocusedTabPane.getTabCount() == 0) {
            lastFocusedTabPane = findFirstTabPane();
        }

        lastFocusedTab = lastFocusedTabPane.getFocusedTab();
        if (tmpLastFocusTab != lastFocusedTab) {
            notifyTabChange();
        }
    }

    private TabbedPaneWrapper findFirstTabPane() {
        Component component = root.getComponent();

        while (!(component instanceof DnDTabbedPane)) {
            if (component instanceof JSplitPane) {
                component = ((JSplitPane) component).getLeftComponent();
            } else if (component instanceof JPanel) {
                component = ((JPanel) component).getComponent(0);
            }
        }

        return ((DnDTabbedPane) component).getTabbedPane();
    }

    private void setupFocusListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(
                new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                if ("focusOwner".equals(evt.getPropertyName())) {
                    final Component owner = (Component) evt.getNewValue();
                    final DnDTabbedPane lastTabbedPane = findParent(owner);
                    if (lastTabbedPane != null) {
                        final TabbedPaneWrapper tabbedPane = lastTabbedPane.getTabbedPane();
                        setLastFocus(tabbedPane, tabbedPane.getFocusedTab());
                    }
                }
            }
        });
    }

    private DnDTabbedPane findParent(Component ownerParent) {
        DnDTabbedPane lastTabbedPane = null;

        while(ownerParent != null) {
            if (ownerParent instanceof DnDTabbedPane) {
                lastTabbedPane = (DnDTabbedPane) ownerParent;
            }
            if (ownerParent == root.getComponent()) {
                return lastTabbedPane;
            }

            ownerParent = ownerParent.getParent();
        }
        return null;
    }

    public Tab getActiveTab() {
        return lastFocusedTab;
    }

    public Tab[] getFocusedTabs() {
        final List<Tab> focusedTabs = new ArrayList<>();
        for (final TabbedPaneWrapper tabbedPane : root.findTabPanes()) {
            final Tab tab = tabbedPane.getFocusedTab();
            if (tab != null) {
                focusedTabs.add(tab);
            }
        }
        return focusedTabs.toArray(new Tab[focusedTabs.size()]);
    }

    public Tab[] getTabs() {
        final List<Tab> tabs = new ArrayList<>();
        for (final TabbedPaneWrapper tabbedPane : root.findTabPanes()) {
            tabs.addAll(tabbedPane.getTabs());
        }
        return tabs.toArray(new Tab[tabs.size()]);
    }

    public void focusTab(final Tab tab) {
        final TabbedPaneWrapper owner = root.findTabOwner(tab);
        owner.setFocus(tab);
    }

    public void addListener(final TabPanelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(final TabPanelListener listener) {
        listeners.remove(listener);
    }

    public boolean containsTab(final Tab tab) {
        return root.findTabOwner(tab) != null;
    }

    public void setKeyListener(final KeyListener newListener) {
        for (final TabbedPaneWrapper tabbedPane : root.findTabPanes()) {
            if (keyListener != null) {
                tabbedPane.getComponent().removeKeyListener(keyListener);
            }
            if (newListener != null) {
                tabbedPane.getComponent().addKeyListener(newListener);
            }
        }

        keyListener = newListener;
    }
}
