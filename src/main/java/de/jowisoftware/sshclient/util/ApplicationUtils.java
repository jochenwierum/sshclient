package de.jowisoftware.sshclient.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;

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
        final Properties properties;
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
            properties = readUpdateProperties();
        } catch (final IOException e) {
            LOGGER.warn("Error while fetching update information", e);
            return "Error while fetching update information: " + e.getMessage();
        }

        final int thisRevision = Integer.parseInt(thisVersion.revision);
        final int newRevision = Integer.parseInt(properties.getProperty("SCM-Revision"));

        if (thisRevision < newRevision) {
            return "Build " + newRevision + " (" +
                properties.getProperty("SCM-Branch") + ", build: " +
                properties.getProperty("Build-Date") + ")";
        } else {
            return null;
        }
    }

    private static Properties readUpdateProperties() throws IOException {
        final Properties properties = new Properties();
        final URL url = new URL(UPDATE_URL);
        final InputStream urlStream = url.openStream();
        properties.load(urlStream);
        urlStream.close();
        return properties;
    }

    private static VersionInformation readVersion() throws IOException {
        final Enumeration<URL> resources = ApplicationUtils.class.getClassLoader()
                .getResources("META-INF/MANIFEST.MF");
        while (resources.hasMoreElements()) {
            final InputStream stream = resources.nextElement().openStream();
            final Manifest manifest = new Manifest(stream);
            stream.close();

            final String revision = manifest.getMainAttributes().getValue("SCM-Revision");
            final String branch = manifest.getMainAttributes().getValue("SCM-Branch");
            final String date = manifest.getMainAttributes().getValue("Build-Date");

            if (revision != null && branch != null && date != null) {
                return new VersionInformation(revision, branch, date);
            }
        }

        return new VersionInformation("unknown", "unknown", "unknown");
    }
}
