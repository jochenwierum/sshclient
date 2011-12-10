package de.jowisoftware.sshclient;

import java.io.File;

import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSch;

import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.application.ApplicationSettings;
import de.jowisoftware.sshclient.application.persisting.XMLLoader;
import de.jowisoftware.sshclient.i18n.Translation;
import de.jowisoftware.sshclient.jsch.JSchKeyManager;
import de.jowisoftware.sshclient.ui.MainWindow;
import de.jowisoftware.sshclient.ui.security.SimplePasswordManager;

public class Init {
    private static final Logger LOGGER = Logger.getLogger(Init.class);

    public void start() {
        setupLookAndFeel();

        final Application application = createApplication();
        showWindow(application);
        initApplication(application);
    }

    private void setupLookAndFeel() {
        try {
            final String nativeLF = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(nativeLF);
        } catch (final Exception e) {
            LOGGER.error("Could not set Look&Feel", e);
        }
    }

    private Application createApplication() {
        final JSch jsch = new JSch();
        final ApplicationSettings settings = new ApplicationSettings();
        final SimplePasswordManager passwordManager = new SimplePasswordManager();
        final JSchKeyManager keyManager = new JSchKeyManager(jsch,
                settings, passwordManager);
        final Application application = new Application(jsch, settings,
                passwordManager, keyManager);
        return application;
    }

    private void showWindow(final Application application) {
        initTranslation(application);
        final MainWindow mainWindow = new MainWindow(application);
        mainWindow.setVisible(true);
    }

    private void initTranslation(final Application application) {
        final String language = application.settings.getLanguage();
        Translation.initStaticTranslationWithLanguage(language);
    }

    private void initApplication(final Application application) {
        final File settingsFile = new File(application.sshDir, "settings.xml");
        if (settingsFile.isFile()) {
            new XMLLoader(application.settings).load(settingsFile);
            application.profileEvents.fire().profilesUpdated();
        }

        application.importKeys();
    }
}
