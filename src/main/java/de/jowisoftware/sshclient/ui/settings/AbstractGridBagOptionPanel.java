package de.jowisoftware.sshclient.ui.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

@SuppressWarnings("serial")
abstract class AbstractGridBagOptionPanel extends AbstractOptionPanel {
    public AbstractGridBagOptionPanel() {
        setLayout(new GridBagLayout());
    }

    protected void fillToBottom(final int y) {
        final GridBagConstraints constraints = makeConstraints(1, y);
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.weighty = 1.0;
        constraints.anchor = GridBagConstraints.NORTH;
        add(new JLabel(""), constraints);
    }

    protected GridBagConstraints makeConstraints(final int x, final int y) {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        return constraints;
    }
}
