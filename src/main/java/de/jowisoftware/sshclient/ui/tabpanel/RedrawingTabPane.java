package de.jowisoftware.sshclient.ui.tabpanel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import de.jowisoftware.sshclient.util.Constants;

public class RedrawingTabPane extends DnDTabbedPane {
    private static final long serialVersionUID = 4839537888220784886L;
    private final Timer redrawTimer;

    public RedrawingTabPane() {
        redrawTimer = createTimer();
    }

    private Timer createTimer() {
        final Timer renderTimer = new Timer(Constants.RERENDER_TIMER_MS, new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Component component = getSelectedComponent();
                if (component instanceof Redrawable) {
                    ((Redrawable) component).redraw();
                }
            }
        });

        renderTimer.setRepeats(true);
        renderTimer.start();
        return renderTimer;
    }

    public void restartTimer() {
        /*
         * the inverted screen should be visible for 200 ms (and not 399,
         * which would be possible if the timer fires 199ms after the rendering)
         * so the timer is restarted
         */
        redrawTimer.restart();
    }

    public void stopRedraw() {
        redrawTimer.stop();
    }
}
