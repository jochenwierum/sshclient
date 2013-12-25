package de.jowisoftware.sshclient.ui;

import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.SystemColor;

public class CloseButton extends JButton {
    private static final long serialVersionUID = 1908007762676130681L;
    private static final int SIZE = 12;

    public CloseButton() {
        setBorderPainted(false);
        setFocusable(false);
        setContentAreaFilled(false);
        setPreferredSize(new Dimension(SIZE, SIZE));
    }

    @Override
    public void paintComponent(final Graphics g) {
        if(getModel().isPressed()) {
            g.setColor(SystemColor.controlShadow);
            g.drawLine(0, 0, SIZE - 1, 0);
            g.drawLine(0, 0, 0, SIZE - 1);
            g.setColor(SystemColor.controlHighlight);
            g.drawLine(SIZE - 1, 0, SIZE - 1, SIZE - 1);
            g.drawLine(0, SIZE - 1, SIZE - 1, SIZE - 1);
        } else if (getModel().isRollover()) {
            g.setColor(SystemColor.controlHighlight);
            g.drawLine(0, 0, SIZE - 1, 0);
            g.drawLine(0, 0, 0, SIZE - 1);
            g.setColor(SystemColor.controlShadow);
            g.drawLine(SIZE - 1, 0, SIZE - 1, SIZE - 1);
            g.drawLine(0, SIZE - 1, SIZE - 1, SIZE - 1);
        }

        g.setColor(SystemColor.controlText);
        if (getModel().isPressed()) {
            g.translate(1, 1);
        }
        g.drawLine(2, 2, SIZE - 3, SIZE - 3);
        g.drawLine(SIZE - 3, 2, 2, SIZE - 3);
    }
}
