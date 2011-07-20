package de.jowisoftware.sshclient.settings;

import java.io.File;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

public class KeyAgentManager {
    private static final Logger LOGGER = Logger.getLogger(KeyAgentManager.class);
    private final JSch jsch;

    public KeyAgentManager(final JSch jsch) {
        this.jsch = jsch;
    }

    public void persistKeyListToSettings(final ApplicationSettings settings) {
        try {
            LOGGER.info("Persisting " + jsch.getIdentityNames().size() + " private keys");

            settings.getKeyFiles().clear();
            for (final Object keyName : jsch.getIdentityNames()) {
                settings.getKeyFiles().add(new File((String) keyName));
            }
        } catch (final JSchException e) {
            LOGGER.error("Could not persist keylist", e);
        }
    }

    public void loadKeyListFromSettings(final ApplicationSettings settings) {
        if (!settings.isUnlockKeysOnStart()) {
            loadKeysWithoutUnlocking(settings);
        } else {
            // TODO: change this
            loadKeysWithoutUnlocking(settings);
        }
    }

    private void loadKeysWithoutUnlocking(final ApplicationSettings settings) {
        for (final File file : settings.getKeyFiles()) {
            try {
                LOGGER.info("Restoring private key: " + file.getAbsolutePath());
                jsch.addIdentity(file.getAbsolutePath());
            } catch (final JSchException e) {
                LOGGER.error("Could not load key: " + file.getAbsolutePath(), e);
            }
        }
    }
}
