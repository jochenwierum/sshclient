package de.jowisoftware.ssh.client.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.jowisoftware.ssh.client.util.StringUtils;

public class InfoPane extends JPanel {
    private static final long serialVersionUID = -4396620050414029131L;

    public InfoPane(final String message) {
        init(message);
    }

    public void init(final String text) {
        setAlignmentX(CENTER_ALIGNMENT);
        setAlignmentY(CENTER_ALIGNMENT);

        add(new JLabel(StringUtils.multiLineText2JTextString(text)));
    }

}
