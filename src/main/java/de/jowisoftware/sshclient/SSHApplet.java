package de.jowisoftware.sshclient;

import javax.swing.UIManager;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.ui.MainWindow;

public class SSHApplet extends java.applet.Applet {
    private static final long serialVersionUID = -4143101114325971711L;
    private static final Logger LOGGER = Logger.getLogger(SSHApplet.class);

    @Override
    @SuppressWarnings("unused")
    public void init() {
        try {
            final String nativeLF = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(nativeLF);
        } catch (final Exception e) {
            LOGGER.error("Could not set Look&Feel", e);
        }

        super.init();
        new MainWindow();
    }
}
