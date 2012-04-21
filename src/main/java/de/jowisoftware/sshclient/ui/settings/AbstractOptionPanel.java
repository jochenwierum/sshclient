package de.jowisoftware.sshclient.ui.settings;

import javax.swing.JPanel;

@SuppressWarnings("serial")
abstract class AbstractOptionPanel extends JPanel {
    public AbstractOptionPanel() {
        setOpaque(false);
    }

    abstract public String getTitle();
    void save() { /* empty by default */ }
}
