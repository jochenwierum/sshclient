package de.jowisoftware.sshclient.ui;

import de.jowisoftware.sshclient.util.StringUtils;

import javax.swing.*;

public class InfoPane extends JPanel {
    private static final long serialVersionUID = -4396620050414029131L;

    public InfoPane(final String message) {
        init(message);
    }

    private void init(final String text) {
        setAlignmentX(CENTER_ALIGNMENT);
        setAlignmentY(CENTER_ALIGNMENT);

        add(new JLabel(StringUtils.multiLineText2JTextString(text)));
    }

}
