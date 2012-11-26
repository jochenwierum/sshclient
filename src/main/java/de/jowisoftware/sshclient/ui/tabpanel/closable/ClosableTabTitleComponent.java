package de.jowisoftware.sshclient.ui.tabpanel.closable;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.SystemColor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.jowisoftware.sshclient.ui.tabpanel.Tab;

public class ClosableTabTitleComponent extends JPanel implements MouseListener {
    private static final long serialVersionUID = 4533946005667886601L;

    private final Tab tab;
    protected final JLabel label;
    private JButton button;

    private final List<ClosableTabListener> listeners =
            new ArrayList<ClosableTabListener>();

    public ClosableTabTitleComponent(final Tab tab, final JLabel label) {
        this.tab = tab;
        this.label = label;
        setOpaque(false);
        addComponents();
    }

    protected final void addComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        label.setOpaque(false);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
        add(label);
        add(createCloseButton());
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

    public void addListener(final ClosableTabListener listener) {
        listeners.add(listener);
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        for (final ClosableTabListener listener : listeners) {
            listener.closeTab(tab);
        }
    }

    @Override public void mouseEntered(final MouseEvent e) { /* ignored */ }
    @Override public void mouseExited(final MouseEvent e) { /* ignored */ }
    @Override public void mousePressed(final MouseEvent e) { /* ignored */ }
    @Override public void mouseReleased(final MouseEvent e) { /* ignored */ }
}