package de.jowisoftware.sshclient.ui.settings;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public abstract class AbstractOptionPanel extends JPanel {
    public AbstractOptionPanel() {
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
