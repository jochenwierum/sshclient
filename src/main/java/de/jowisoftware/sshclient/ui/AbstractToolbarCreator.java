package de.jowisoftware.sshclient.ui;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import java.net.URL;

public class AbstractToolbarCreator {
    protected final JToolBar toolBar;

    protected AbstractToolbarCreator(final String name) {
        toolBar = new JToolBar(name);
    }

    protected JSeparator createSeparator() {
        return new JSeparator(SwingConstants.VERTICAL);
    }

    protected JButton createButton(final String text, final String image) {
        final URL icon = getClass().getResource("/gfx/" + image + ".png");
        final JButton button = new JButton();
        button.setIcon(new ImageIcon(icon, text));
        button.setToolTipText(text);
        return button;
    }

    public JToolBar getToolBar() {
        return toolBar;
    }


}
