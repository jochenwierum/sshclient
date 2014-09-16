package de.jowisoftware.sshclient.ui;

import de.jowisoftware.sshclient.util.StringUtils;

import javax.swing.*;

public class ErrorPane extends JPanel {
    private static final long serialVersionUID = -796506188029383907L;

    public ErrorPane(final String message, final Throwable e) {
        init(message + "\n\n" + e.getMessage());
    }

    private void init(final String text) {
        setAlignmentX(CENTER_ALIGNMENT);
        setAlignmentY(CENTER_ALIGNMENT);

        add(new JLabel(StringUtils.multiLineText2JTextString(text)));
    }
}
