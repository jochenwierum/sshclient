package de.jowisoftware.sshclient.ui.tabpanel.redrawing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import de.jowisoftware.sshclient.ui.tabpanel.Tab;
import de.jowisoftware.sshclient.ui.tabpanel.TabPanel;
import de.jowisoftware.sshclient.util.Constants;

public class RedrawingTabPanel extends TabPanel {
    private final Timer redrawTimer;

    public RedrawingTabPanel() {
        redrawTimer = createTimer();
    }

    private Timer createTimer() {
        final Timer renderTimer = new Timer(Constants.RERENDER_TIMER_MS, new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                for (final Tab tab : getFocusedTabs()) {
                    if (tab instanceof RedrawableTab) {
                        ((RedrawableTab) tab).redraw();
                    }
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
