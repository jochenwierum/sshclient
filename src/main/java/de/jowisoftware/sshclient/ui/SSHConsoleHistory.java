package de.jowisoftware.sshclient.ui;

import java.awt.Adjustable;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JScrollBar;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.mouse.MouseCursorManager;

public class SSHConsoleHistory implements AdjustmentListener, MouseWheelListener {
    private final JScrollBar scrollBar = new JScrollBar(Adjustable.VERTICAL, 0,
            24, 0, 24);
    private final SSHSession session;
    private final MouseCursorManager mouseCursorManager;

    public SSHConsoleHistory(final SSHSession session,
            final MouseCursorManager mouseCursorManager) {
        this.session = session;
        this.mouseCursorManager = mouseCursorManager;

        scrollBar.setBlockIncrement(24);
        scrollBar.setEnabled(false);
        scrollBar.addAdjustmentListener(this);
        renderOffsetChanged();
    }

    public JScrollBar getScrollBar() {
        return scrollBar;
    }

    public void updateHistorySize(final int max) {
        scrollBar.getModel().setRangeProperties(0, scrollBar.getVisibleAmount(),
                -max, scrollBar.getMaximum(), true);
        scrollBar.setEnabled(-scrollBar.getMinimum() > 0);
        renderOffsetChanged();
    }

    public void updateWindowSize(final int ch) {
        scrollBar.getModel().setRangeProperties(0, ch, scrollBar.getMinimum(), ch, true);
        scrollBar.setBlockIncrement(ch);
        scrollBar.setEnabled(-scrollBar.getMinimum() > 0);
        renderOffsetChanged();
    }

    private void renderOffsetChanged() {
        session.setRenderOffset(-scrollBar.getValue());
        mouseCursorManager.setRenderOffset(-scrollBar.getValue());
    }

    @Override
    public void adjustmentValueChanged(final AdjustmentEvent e) {
        renderOffsetChanged();
    }

    public void scrollDown() {
        down(1);
    }

    public void scrollUp() {
        up(1);
    }

    public void scrollPageDown() {
        down(scrollBar.getBlockIncrement());
    }

    public void scrollPageUp() {
        up(scrollBar.getBlockIncrement());
    }

    private void down(final int amount) {
        scrollBar.setValue(scrollBar.getValue() + amount);
    }

    private void up(final int amount) {
        scrollBar.setValue(scrollBar.getValue() - amount);
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        final int amount;
        if(e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
            amount = e.getUnitsToScroll();
        } else {
            amount = scrollBar.getBlockIncrement() * e.getWheelRotation();
        }

        if (amount > 0) {
            up(-amount);
        } else if (amount < 0) {
            down(amount);
        }
    }

}
