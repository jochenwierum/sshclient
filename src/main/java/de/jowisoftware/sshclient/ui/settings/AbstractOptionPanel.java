package de.jowisoftware.sshclient.ui.settings;

import javax.swing.*;
import java.awt.*;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

@SuppressWarnings("serial")
public abstract class AbstractOptionPanel extends JPanel {
    protected final Window parentWindow;

    protected AbstractOptionPanel(final Window parent) {
        this.parentWindow = parent;
        setOpaque(false);
    }

    abstract public String getTitle();
    public void save() { /* empty by default */ }

    protected JLabel blind() {
        return new JLabel();
    }

    protected JLabel label(final String key, final String text) {
        final JLabel label = new JLabel(t(key, text));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    protected JLabel label(final String key, final String text,
            final char mnemonic, final Component targetComponent) {
        final JLabel label = label(key, text);
        label.setDisplayedMnemonic(m(key, mnemonic));
        label.setLabelFor(targetComponent);
        return label;
    }
}
