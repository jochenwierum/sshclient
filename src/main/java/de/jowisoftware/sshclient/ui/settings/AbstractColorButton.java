package de.jowisoftware.sshclient.ui.settings;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;

public abstract class AbstractColorButton extends JButton implements ActionListener {
    private static final long serialVersionUID = 5918403991063984914L;
    private static final int BUTTON_SIZE = 20;

    public AbstractColorButton(final Color initialColor) {
        super("change");
        setBackground(initialColor);

        final Dimension size = getMinimumSize();
        size.height = BUTTON_SIZE;
        size.width = BUTTON_SIZE;
        setMinimumSize(size);
        setPreferredSize(size);

        addActionListener(this);
    };

    @Override
    protected void paintComponent(final Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final JButton source = (JButton) e.getSource();
        final Color newColor = JColorChooser.showDialog(AbstractColorButton.this,
                t("profiles.gfx.choosecolor", "choose color"),
                source.getBackground());

        if (newColor != null) {
            source.setBackground(newColor);
            saveColor(newColor, e);
        }
    }

    protected abstract void saveColor(Color newColor, ActionEvent e);
}
