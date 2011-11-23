package de.jowisoftware.sshclient.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

public class JarUtils {
    private JarUtils() { /* Util classes will not be instanciated */ }

    public static String getVersion() {
        String version = "";
        try {
            final Enumeration<URL> resources = JarUtils.class.getClassLoader()
                    .getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements() && version.isEmpty()) {
                final InputStream stream = resources.nextElement().openStream();
                final Manifest manifest = new Manifest(stream);
                stream.close();

                final String revision = manifest.getMainAttributes().getValue("SCM-Revision");
                final String branch = manifest.getMainAttributes().getValue("SCM-Branch");
                final String date = manifest.getMainAttributes().getValue("Build-Date");

                if (revision != null && branch != null && date != null) {
                    version = branch + "-" + revision + " " + date;
                }
            }
        } catch (final IOException e) {
            return "(Error while reading jar file)";
        }

        if (version.isEmpty()) {
            return "(unkown version)";
        } else {
            return version;
        }
    }
}
