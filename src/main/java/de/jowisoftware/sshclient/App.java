package de.jowisoftware.sshclient;

import javax.swing.UIManager;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.ui.MainWindow;

public class App {
    private App() { /* This class cannot be intantiated */ }
    private static final Logger LOGGER = Logger.getLogger(App.class);

    @SuppressWarnings("unused")
    public static void main(final String[] args) {
        try {
            final String nativeLF = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(nativeLF);
        } catch (final Exception e) {
            LOGGER.error("Could not set Look&Feed", e);
        }

        new MainWindow();
    }
}
