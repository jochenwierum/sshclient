package de.jowisoftware.sshclient.settings;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

public class KeyAgentManager {
    private static final Logger LOGGER = Logger.getLogger(KeyAgentManager.class);
    private final JSch jsch;

    public KeyAgentManager(final JSch jsch) {
        this.jsch = jsch;
    }

    public void persistKeyListToFile(final File file) {
        try {
            LOGGER.info("Persisting " + jsch.getIdentityNames().size() + " private keys");
            FileUtils.writeLines(file, jsch.getIdentityNames());
        } catch (final IOException e) {
            LOGGER.error("Could not persist keylist", e);
        } catch (final JSchException e) {
            LOGGER.error("Could not persist keylist", e);
        }
    }

    public void loadKeyListFromFile(final File file) {
        try {
            for (final Object keyLine : FileUtils.readLines(file)) {
                try {
                    LOGGER.info("Restoring private key: " + keyLine);
                    jsch.addIdentity((String) keyLine);
                } catch (final JSchException e) {
                    LOGGER.error("Could not load key: " + keyLine, e);
                }
            }
        } catch (final IOException e) {
            LOGGER.error("Could not load keylist", e);
        }
    }
}
