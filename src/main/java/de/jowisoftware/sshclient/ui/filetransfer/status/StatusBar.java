package de.jowisoftware.sshclient.ui.filetransfer.status;

import de.jowisoftware.sshclient.async.StatusListener;
import de.jowisoftware.sshclient.util.SwingUtils;

import javax.swing.JComponent;
import javax.swing.JLabel;
import java.util.Deque;
import java.util.LinkedList;

import static de.jowisoftware.sshclient.i18n.Translation.t;

public class StatusBar implements StatusListener {
    private final JLabel label = new JLabel();
    private final Deque<String> status = new LinkedList<>();

    public StatusBar() {
        status.addFirst(t("ready", "ready"));
    }

    @Override
    public void beginAction(final String text) {
        status.addFirst(text + " ...");
        updateContent();
    }

    @Override
    public void endAction(final String name) {
        status.remove(name + " ...");
        updateContent();
    }

    private void updateContent() {
        final String text = status.peekFirst();
        SwingUtils.runDelayedInSwingThread(new Runnable() {
            @Override
            public void run() {
                label.setText(text);
            }
        });
    }

    public JComponent getComponent() {return label;}
}
