package de.jowisoftware.sshclient.jsch;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import de.jowisoftware.sshclient.application.PasswordManager;
import de.jowisoftware.sshclient.application.UserAbortException;
import de.jowisoftware.sshclient.application.settings.ApplicationSettings;
import de.jowisoftware.sshclient.application.settings.KeyManager;
import de.jowisoftware.sshclient.application.settings.KeyManagerEvents;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.events.EventHub;
import de.jowisoftware.sshclient.events.EventHubClient;
import de.jowisoftware.sshclient.events.ReflectionEventHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class JSchKeyManager implements KeyManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSchKeyManager.class);
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
            LOGGER.info("Persisting {} private keys", jsch.getIdentityNames().size());

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
            LOGGER.info("Restoring private key: {}", filePath);

            loadKey(filePath);
        }
    }

    private String getKeyPassword(final String keyName) throws UserAbortException {
        final KeyPair key;
        try {
            key = KeyPair.load(jsch, keyName);
        } catch (final JSchException e) {
            LOGGER.error("Could not open key: {}", keyName, e);
            return null;
        }

        if (key.isEncrypted()) {
            return requestPassword(keyName, key);
        } else {
            return null;
        }
    }

    private String requestPassword(final String keyName, final KeyPair key) throws UserAbortException {
        String password;
        boolean firstTime = true;

        do {
            password = passwordManager.getPassword(keyName, !firstTime);
            firstTime = false;
        } while(!key.decrypt(password));

        return password;
    }

    @Override
    public void loadKey(final String absolutePath) {
        if (isKeyAlreadyLoaded(absolutePath)) {
            return;
        }

        try {
            final String password = getKeyPassword(absolutePath);
            jsch.addIdentity(absolutePath, password);
            events.fire().keysUpdated();
        } catch(final JSchException e) {
            LOGGER.error("Could not load key: {}", absolutePath, e);
        } catch (final UserAbortException e) {
            LOGGER.info("Not loading key - user aborted password input: {}", absolutePath);
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
