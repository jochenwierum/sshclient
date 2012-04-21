package de.jowisoftware.sshclient.ui.settings;

import javax.swing.JLabel;

public class RightAlignedLabel extends JLabel {
    private static final long serialVersionUID = 6055372370101505152L;

    public RightAlignedLabel(final String text) {
        super(text);
        setAlignmentX(RIGHT_ALIGNMENT);
    }
}
