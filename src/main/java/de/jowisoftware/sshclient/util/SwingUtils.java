package de.jowisoftware.sshclient.util;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public final class SwingUtils {
    private SwingUtils() {
        /* Util classes will not be instanciated */
    }

    public static void runInSwingThread(final Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (final InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void showMessage(final JFrame parent, final String message,
            final String title, final int messageType) {

        runInSwingThread(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(parent, message, title,
                        messageType);
            }
        });
    }
}
