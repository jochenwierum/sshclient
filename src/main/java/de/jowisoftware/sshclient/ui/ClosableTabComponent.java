package de.jowisoftware.sshclient.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.SystemColor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ClosableTabComponent extends JPanel implements MouseListener {
    private static final long serialVersionUID = 227847073462546872L;

    private JButton button;

    protected final JComponent parent;
    protected final JTabbedPane pane;
    protected final String title;

    public ClosableTabComponent(final String title, final JComponent parent, final JTabbedPane pane) {
        this.parent = parent;
        this.title = title;
        this.pane = pane;

        init();
    }

    protected final void init() {
        setOpaque(false);
        addComponents();
    }

    private final void addComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        add(createLabel());
        add(createCloseButton());
    }

    protected JLabel createLabel() {
        final JLabel label = new JLabel(title);
        label.setOpaque(false);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
        return label;
    }

    private JButton createCloseButton() {
        final int size = 12;
        button = new JButton() {
            private static final long serialVersionUID = 1808007762676130681L;

            @Override
            public void paintComponent(final Graphics g) {
                if(getModel().isPressed()) {
                    g.setColor(SystemColor.controlShadow);
                    g.drawLine(0, 0, size - 1, 0);
                    g.drawLine(0, 0, 0, size - 1);
                    g.setColor(SystemColor.controlHighlight);
                    g.drawLine(size - 1, 0, size - 1, size - 1);
                    g.drawLine(0, size - 1, size - 1, size - 1);
                } else if (getModel().isRollover()) {
                    g.setColor(SystemColor.controlHighlight);
                    g.drawLine(0, 0, size - 1, 0);
                    g.drawLine(0, 0, 0, size - 1);
                    g.setColor(SystemColor.controlShadow);
                    g.drawLine(size - 1, 0, size - 1, size - 1);
                    g.drawLine(0, size - 1, size - 1, size - 1);
                }

                g.setColor(SystemColor.controlText);
                if (getModel().isPressed()) {
                    g.translate(1, 1);
                }
                g.drawLine(2, 2, size - 3, size - 3);
                g.drawLine(size - 3, 2, 2, size - 3);
            }
        };

        button.addMouseListener(this);
        button.setBorderPainted(false);
        button.setFocusable(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(size, size));
        return button;
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        pane.remove(parent);
    }

    @Override public void mouseEntered(final MouseEvent e) { /* ignored */ }
    @Override public void mouseExited(final MouseEvent e) { /* ignored */ }
    @Override public void mousePressed(final MouseEvent e) { /* ignored */ }
    @Override public void mouseReleased(final MouseEvent e) { /* ignored */ }

}