package de.jowisoftware.sshclient;

import javax.swing.UIManager;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.ui.MainWindow;

public class SSHApp {
    private SSHApp() { /* This class cannot be intantiated */ }
    private static final Logger LOGGER = Logger.getLogger(SSHApp.class);

    @SuppressWarnings("unused")
    public static void main(final String[] args) {
        try {
            final String nativeLF = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(nativeLF);
        } catch (final Exception e) {
            LOGGER.error("Could not set Look&Feel", e);
        }

        new MainWindow();
    }
}
