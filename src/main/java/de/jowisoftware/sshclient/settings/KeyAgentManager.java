package de.jowisoftware.sshclient.settings;

import java.io.File;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

public class KeyAgentManager {
    private static final Logger LOGGER = Logger.getLogger(KeyAgentManager.class);
    private final JSch jsch;
    private final ApplicationSettings settings;

    public KeyAgentManager(final JSch jsch, final ApplicationSettings settings) {
        this.jsch = jsch;
        this.settings = settings;
    }

    public void persistKeyListToSettings() {
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

    public void loadKeyListFromSettings() {
        for (final File file : settings.getKeyFiles()) {
            LOGGER.info("Restoring private key: " + file.getAbsolutePath());
            loadKey(file.getAbsolutePath());
        }
    }

    public void loadKey(final String absolutePath) {
        if (isKeyAlreadyLoaded(absolutePath)) {
            return;
        }

        try {
            jsch.addIdentity(absolutePath);
        } catch(final JSchException e) {
            LOGGER.error("Could not load key: " + absolutePath, e);
        }
    }

    public void removeIdentity(final String name) {
        try {
            jsch.removeIdentity(name);
        } catch(final JSchException e) {
            LOGGER.error("Error while removing identity", e);
        }
    }

    public boolean isKeyAlreadyLoaded(final String fullPath) {
        final String search = fullPath.toLowerCase();
        try {
            for (final Object o : jsch.getIdentityNames()) {
                LOGGER.debug(o + " vs " + search);
                if (search.equals(((String) o).toLowerCase())) {
                    return true;
                }
            }
        } catch (final JSchException e) {
            LOGGER.error("Could not iterate loaded keys", e);
        }
        return false;
    }
}
