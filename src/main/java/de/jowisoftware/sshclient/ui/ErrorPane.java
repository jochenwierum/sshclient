package de.jowisoftware.sshclient.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.jowisoftware.sshclient.util.StringUtils;

public class ErrorPane extends JPanel {
    private static final long serialVersionUID = -796506188029383907L;

    public ErrorPane(final String message) {
        init(message);
    }

    public ErrorPane(final String message, final Throwable e) {
        init(message + "\n\n" + e.getMessage());
    }

    public void init(final String text) {
        setAlignmentX(CENTER_ALIGNMENT);
        setAlignmentY(CENTER_ALIGNMENT);

        add(new JLabel(StringUtils.multiLineText2JTextString(text)));
    }
}
