package de.jowisoftware.sshclient.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.SSHApp;

public class ApplicationUtils {
    private static final String UPDATE_URL = "http://jowisoftware.de/ssh/build.properties";

    private static final Logger LOGGER = Logger
            .getLogger(ApplicationUtils.class);

    private ApplicationUtils() { /* Util classes will not be instanciated */ }

    private static class VersionInformation {
        public final String revision;
        public final String branch;
        public final String date;

        public VersionInformation(final String revision, final String branch, final String date) {
            this.revision = revision;
            this.branch = branch;
            this.date = date;
        }
    }

    public static String getVersion() {
        final VersionInformation version;
        try {
            version = readVersion();
        } catch(final IOException e) {
            return "(Error while reading jar file)";
        }

        return version.branch + "-" + version.revision + " " + version.date;
    }

    public static String getAvailableUpdateVersion() {
        final VersionInformation updateVersion;
        final VersionInformation thisVersion;

        try {
            thisVersion = readVersion();
        } catch (final IOException e) {
            LOGGER.info("Could not read Manifest. Broken jar file?", e);
            return null;
        }

        if (thisVersion.revision.isEmpty()) {
            LOGGER.info("No SCM-Build, skipping update check");
            return null;
        }

        try {
            updateVersion = readUpdateProperties();
        } catch (final IOException e) {
            LOGGER.warn("Error while fetching update information", e);
            return "Error while fetching update information: " + e.getMessage();
        }

        if (!thisVersion.revision.equals(updateVersion.revision)) {
            return "Build " + updateVersion.revision + " (built: " +
                    updateVersion.date + ")";
        } else {
            return null;
        }
    }

    private static VersionInformation readUpdateProperties() throws IOException {
        final URL url = new URL(UPDATE_URL);
        final InputStream urlStream = url.openStream();
        final VersionInformation result = readFromStream(urlStream, false);
        urlStream.close();
        return result;
    }

    private static VersionInformation readVersion() throws IOException {
        final Enumeration<URL> resources = ApplicationUtils.class.getClassLoader()
                .getResources("META-INF/MANIFEST.MF");
        while (resources.hasMoreElements()) {
            final InputStream stream = resources.nextElement().openStream();
            final VersionInformation result = readFromStream(stream, true);
            stream.close();
            if (result != null) {
                return result;
            }
        }

        return new VersionInformation("unknown", "unknown", "unknown");
    }

    private static VersionInformation readFromStream(final InputStream stream,
            final boolean filter)
            throws IOException {
        final Properties manifest = new Properties();
        manifest.load(stream);

        final String revision = manifest.getProperty("SCM-Revision");
        final String branch = manifest.getProperty("SCM-Branch");
        final String date = manifest.getProperty("Build-Date");
        final String mainClass = manifest.getProperty("Main-Class");

        final boolean isSSHManifest = mainClass != null && SSHApp.class.getName().equals(mainClass);
        final boolean containsVersionInformation = revision != null && branch != null && date != null;

        if (!filter || (isSSHManifest && containsVersionInformation)) {
            return new VersionInformation(revision, branch, date);
        }
        return null;
    }
}
