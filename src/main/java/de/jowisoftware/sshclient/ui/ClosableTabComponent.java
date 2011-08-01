package de.jowisoftware.sshclient.ui;

import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JTabbedPane;

public class ClosableTabComponent extends AbstractClosableTabcomponent implements MouseListener {
    private static final long serialVersionUID = 227847073462546872L;

    protected final String title;

    public ClosableTabComponent(final String title, final JTabbedPane pane) {
        super(pane);
        this.title = title;
    }

    @Override
    protected JLabel createLabel() {
        return new JLabel(title);
    }
}