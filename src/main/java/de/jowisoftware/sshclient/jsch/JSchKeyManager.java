package de.jowisoftware.sshclient.jsch;

import java.io.File;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

import de.jowisoftware.sshclient.application.ApplicationSettings;
import de.jowisoftware.sshclient.application.KeyManager;
import de.jowisoftware.sshclient.application.KeyManagerEvents;
import de.jowisoftware.sshclient.application.PasswordManager;
import de.jowisoftware.sshclient.application.UserAbortException;
import de.jowisoftware.sshclient.events.EventHub;
import de.jowisoftware.sshclient.events.EventHubClient;
import de.jowisoftware.sshclient.events.ReflectionEventHub;
import de.jowisoftware.sshclient.ui.terminal.AWTProfile;

public class JSchKeyManager implements KeyManager {
    private static final Logger LOGGER = Logger.getLogger(JSchKeyManager.class);
    private final JSch jsch;
    private final ApplicationSettings<AWTProfile> settings;
    private final PasswordManager passwordManager;

    private final EventHub<KeyManagerEvents> events = ReflectionEventHub.forEventClass(KeyManagerEvents.class);

    public JSchKeyManager(final JSch jsch,
            final ApplicationSettings<AWTProfile> settings,
            final PasswordManager passwordManager) {
        this.jsch = jsch;
        this.settings = settings;
        this.passwordManager = passwordManager;
    }

    @Override
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

    @Override
    public void loadKeyListFromSettings() {
        for (final File file : settings.getKeyFiles()) {
            final String filePath = file.getAbsolutePath();
            LOGGER.info("Restoring private key: " + filePath);

            String password = null;
            if (settings.getUnlockKeysOnStartup()) {
                password = getKeyPassword(filePath);
            }

            loadKey(filePath, password);
        }
    }

    private String getKeyPassword(final String keyName) {
        KeyPair key;
        try {
            key = KeyPair.load(jsch, keyName);
        } catch (final JSchException e) {
            LOGGER.error("Could not open key: " + keyName, e);
            return null;
        }

        if (key.isEncrypted()) {
            return requestPassword(keyName, key);
        } else {
            return null;
        }
    }

    private String requestPassword(final String keyName, final KeyPair key) {
        String password;
        boolean firstTime = true;

        do {
            try {
                password = passwordManager.getPassword(keyName, !firstTime);
            } catch (final UserAbortException e) {
                return null;
            }
            firstTime = false;
        } while(!key.decrypt(password));

        return password;
    }

    @Override
    public void loadKey(final String absolutePath, final String password) {
        if (isKeyAlreadyLoaded(absolutePath)) {
            return;
        }

        try {
            jsch.addIdentity(absolutePath, password);
            events.fire().keysUpdated();
        } catch(final JSchException e) {
            LOGGER.error("Could not load key: " + absolutePath, e);
        }
    }

    @Override
    public void removeIdentity(final String name) {
        try {
            jsch.removeIdentity(name);
            events.fire().keysUpdated();
        } catch(final JSchException e) {
            LOGGER.error("Error while removing identity", e);
        }
    }

    private boolean isKeyAlreadyLoaded(final String fullPath) {
        final String search = fullPath.toLowerCase();
        try {
            for (final Object o : jsch.getIdentityNames()) {
                if (search.equals(((String) o).toLowerCase())) {
                    return true;
                }
            }
        } catch (final JSchException e) {
            LOGGER.error("Could not iterate loaded keys", e);
        }
        return false;
    }

    @Override
    public EventHubClient<KeyManagerEvents> eventListeners() {
        return events;
    }
}
