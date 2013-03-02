package de.jowisoftware.sshclient.application.settings.persistence.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class SettingsUpdate {
    private static final Logger LOGGER = Logger.getLogger(SettingsUpdate.class);

    private final File settingsDir;

    public SettingsUpdate(final File settingsDir) {
        this.settingsDir = settingsDir;
    }

    public void update() {
        final File settingsFile = new File(settingsDir,
                "settings.xml");

        if (!settingsFile.exists()) {
            return;
        }

        final String version = getVersion(settingsFile);
        if (version == null) {
            LOGGER.error("Clould not extract version - skipping update");
        }

        checkVersion(version);
    }

    private String getVersion(final File settingsFile) {
        try (InputStream is = new FileInputStream(settingsFile)) {
            final DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.parse(is);
            final Element rootElement = (Element) doc.getElementsByTagName(
                    "settings").item(0);

            return rootElement.getAttribute("version");
        } catch (final ParserConfigurationException | IOException
                | SAXException e) {
            return null;
        }
    }

    private void checkVersion(final String version) {
        switch (version) {
        case "":
        case "1":
            updateToV2();
        case "2":

            // Do not transform anything - this is the newest version
            break;
        default:
            LOGGER.warn("Unknown version found: " + version);
        }
    }

    private void updateToV2() {
        final File settingsFile = new File(settingsDir, "settings.xml");
        final File backupFile = new File(settingsDir, "settings.v1.bak");
        moveFile(settingsFile, backupFile);
        xslTransform(backupFile, settingsFile,
                "updates/settings/v2/configUpdate.xsl");
    }

    private void xslTransform(final File inputFile, final File outputFile,
            final String xslPath) {
        try (FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream fis = getClass().getClassLoader()
                        .getResourceAsStream(xslPath)) {
            final TransformerFactory tFactory = TransformerFactory
                    .newInstance();
            final Transformer transformer = tFactory.newTransformer(
                    new StreamSource(fis));
            transformer.transform(
                    new StreamSource(inputFile), new StreamResult(fos));
        } catch (final Exception e) {
            LOGGER.error("Could not transform XML file", e);
        }
    }

    private void moveFile(final File settingsFile, final File backupFile) {
        settingsFile.renameTo(backupFile);
    }
}
