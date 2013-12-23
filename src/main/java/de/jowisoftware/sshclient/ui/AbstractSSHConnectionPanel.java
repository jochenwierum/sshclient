package de.jowisoftware.sshclient.ui;

import javax.swing.JPanel;

public abstract class AbstractSSHConnectionPanel extends JPanel {
    abstract public void close();

    public abstract void redraw();

    public abstract void unfreeze();

    public abstract void freeze();

    public abstract void connect();

    public abstract void takeFocus();
}
