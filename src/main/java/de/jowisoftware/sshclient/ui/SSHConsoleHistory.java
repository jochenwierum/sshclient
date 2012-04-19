package de.jowisoftware.sshclient.ui;

import java.awt.Adjustable;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import de.jowisoftware.sshclient.terminal.SSHSession;

public class SSHConsoleHistory implements AdjustmentListener {
    private final JScrollBar scrollBar = new JScrollBar(Adjustable.VERTICAL, 0,
            24, 0, 24);
    private final SSHSession session;

    public SSHConsoleHistory(final SSHSession session) {
        this.session = session;

        scrollBar.setBlockIncrement(24);
        scrollBar.setEnabled(false);
        scrollBar.addAdjustmentListener(this);
        renderOffsetChanged();
    }

    public JScrollBar getScrollBar() {
        return scrollBar;
    }

    public void updateHistorySize(final int max) {
        scrollBar.setMinimum(-max);

        scrollBar.setValue(0);
        scrollBar.setEnabled(-scrollBar.getMinimum() > 0);
        session.setRenderOffset(0);
    }

    public void updateWindowSize(final int ch) {
        scrollBar.setVisibleAmount(ch);
        scrollBar.setMaximum(ch);
        scrollBar.setBlockIncrement(ch);

        scrollBar.setValue(0);
        scrollBar.setEnabled(-scrollBar.getMinimum() > 0);
        session.setRenderOffset(0);
    }

    private void renderOffsetChanged() {
        session.setRenderOffset(-scrollBar.getValue());
        session.render();
    }

    @Override
    public void adjustmentValueChanged(final AdjustmentEvent e) {
        renderOffsetChanged();
    }

    public void scrollDown() {
        scrollBar.setValue(scrollBar.getValue() + 1);
        renderOffsetChanged();
    }

    public void scrollUp() {
        scrollBar.setValue(scrollBar.getValue() - 1);
        renderOffsetChanged();
    }

    public void scrollPageDown() {
        scrollBar.setValue(Math.min(0,
                scrollBar.getValue() + scrollBar.getBlockIncrement()));
        renderOffsetChanged();
    }

    public void scrollPageUp() {
        scrollBar.setValue(Math.max(scrollBar.getMinimum(),
                scrollBar.getValue() - scrollBar.getBlockIncrement()));
        renderOffsetChanged();
    }
}
