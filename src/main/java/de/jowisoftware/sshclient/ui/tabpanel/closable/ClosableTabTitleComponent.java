package de.jowisoftware.sshclient.ui.tabpanel.closable;

import de.jowisoftware.sshclient.ui.CloseButton;
import de.jowisoftware.sshclient.ui.tabpanel.Tab;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class ClosableTabTitleComponent extends JPanel implements MouseListener {
    private static final long serialVersionUID = 4533946005667886601L;

    private final Tab tab;
    protected final JLabel label;

    private final List<ClosableTabListener> listeners =
            new ArrayList<>();

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
        final JButton button = new CloseButton();
        button.addMouseListener(this);
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
