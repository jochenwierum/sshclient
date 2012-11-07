package de.jowisoftware.sshclient.ui.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

@SuppressWarnings("serial")
public abstract class AbstractGridBagOptionPanel extends AbstractOptionPanel {
    public AbstractGridBagOptionPanel(final Window parent) {
        super(parent);
        setLayout(new GridBagLayout());
    }

    protected void fillToBottom(final int y) {
        final GridBagConstraints constraints = makeConstraints(1, y);
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.weighty = 1.0;
        constraints.anchor = GridBagConstraints.NORTH;
        add(blind(), constraints);
    }

    protected GridBagConstraints makeConstraints(final int x, final int y) {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        return constraints;
    }

    protected GridBagConstraints makeLabelConstraints(final int y) {
        final GridBagConstraints constraints = makeConstraints(1, y);
        constraints.insets = new Insets(0, 0, 0, 5);
        constraints.anchor = GridBagConstraints.NORTHEAST;
        return constraints;
    }
}
