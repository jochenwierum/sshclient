package de.jowisoftware.sshclient.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.SSHApp;

public class ApplicationUtils {
    private static final String UPDATE_URL = "http://jowisoftware.de/ssh/build.properties";

    private static final Logger LOGGER = Logger
            .getLogger(ApplicationUtils.class);

    private static boolean isLuja;

    private ApplicationUtils() { /* Util classes will not be instanciated */ }

    public static class VersionInformation {
        public final String revision;
        public final String branch;
        public final String date;
        private boolean isUpdatable;

        public VersionInformation(final String revision, final String branch, final String date) {
            this.revision = revision;
            this.branch = branch;
            this.date = date;
        }

        public boolean isUpdatable() {
            return isUpdatable;
        }

        void setSaneUpdate() {
            isUpdatable = true;
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

    public static VersionInformation getAvailableUpdateVersion() {
        final VersionInformation updateVersion;
        final VersionInformation thisVersion;

        try {
            thisVersion = readVersion();
        } catch (final IOException e) {
            LOGGER.info("Could not read Manifest. Broken jar file?", e);
            return null;
        }

        try {
            updateVersion = readUpdateProperties();
        } catch (final IOException e) {
            LOGGER.warn("Error while fetching update information", e);
            return null;
        }

        if (thisVersion.revision.isEmpty()) {
            LOGGER.info("No SCM-Build, skipping update check");
            return updateVersion;
        }

        if (!thisVersion.revision.equals(updateVersion.revision)) {
            updateVersion.setSaneUpdate();
            return updateVersion;
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

        return new VersionInformation("", "unknown", "unknown");
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

    public static void saveStartupMethod() {
        isLuja = isLuja();
    }

    public static boolean isUsingLuja() {
        return isLuja;
    }

    private static boolean isLuja() {
        final Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
        final Thread mainThread = findMainThread(stackTraces);

        final StackTraceElement firstFrame = stackTraces.get(mainThread)[0];
        return "de.jowisoftware.luja.Main".equals(firstFrame.getClassName());
    }

    private static Thread findMainThread(final Map<Thread, StackTraceElement[]> stackTraces) {
        for (final Thread thread : stackTraces.keySet()) {
            if (thread.getName().equals("main")) {
                return thread;
            }
        }
        throw new IllegalStateException("This softare assumes a main-thread, " +
                "which was not found");
    }
}
