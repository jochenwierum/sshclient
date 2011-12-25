package de.jowisoftware.sshclient.application;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import de.jowisoftware.sshclient.events.EventHub;
import de.jowisoftware.sshclient.events.ReflectionEventHub;

public class Application {
    private static final Logger LOGGER = Logger.getLogger(Application.class);

    public final JSch jsch;

    public final ApplicationSettings settings;
    public final PasswordManager passwordManager;
    public final KeyManager keyManager;

    public final File sshDir;
    public JFrame mainWindow;

    public final EventHub<ProfileEvent> profileEvents =
            ReflectionEventHub.forEventClass(ProfileEvent.class);

    public Application(final JSch jsch, final ApplicationSettings settings,
            final PasswordManager passwordManager, final KeyManager keyManager) {
        this.settings = settings;
        this.passwordManager = passwordManager;
        this.keyManager = keyManager;
        this.jsch = jsch;

        this.passwordManager.init(this);
        sshDir = prepareSSHDir();

        try {
            initJSch();
        } catch(final JSchException e) {
            LOGGER.error("Could not initialise JSch", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "SSH",
                    JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }


    private File prepareSSHDir() {
        final File home = new File(System.getProperty("user.home"));

        final File finalProjectDir = new File(home, ".ssh");
        if (finalProjectDir.isDirectory()) {
            if (!finalProjectDir.exists()) {
                if(!finalProjectDir.mkdir()) {
                    throw new RuntimeException("Could not create directory: " +
                            finalProjectDir.getAbsolutePath());
                }
            }
        }

        return finalProjectDir;
    }

    private void initJSch() throws JSchException {
        JSch.setLogger(new JschLogger());
        jsch.setKnownHosts(new File(sshDir, "known_hosts").getAbsolutePath());
    }

    private void loadDefaultKey() {
        final File privKey = new File(sshDir, "id_rsa");
        if (privKey.isFile()) {
            keyManager.loadKey(privKey.getAbsolutePath(), null);
        }
    }

    public void setMainWindow(final JFrame newMainWindow) {
        this.mainWindow = newMainWindow;
    }

    public void importKeys() {
        if (mainWindow == null) {
            throw new RuntimeException("Mainwindow must be initialized");
        }

        keyManager.loadKeyListFromSettings();
        loadDefaultKey();
    }
}
