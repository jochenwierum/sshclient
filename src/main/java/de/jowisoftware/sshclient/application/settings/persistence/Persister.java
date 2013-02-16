package de.jowisoftware.sshclient.application.settings.persistence;

import de.jowisoftware.sshclient.application.settings.persistence.annotations.PersistenceAnnotationTraverser;
import de.jowisoftware.sshclient.application.settings.persistence.xml.DocumentReader;
import de.jowisoftware.sshclient.application.settings.persistence.xml.XMLDocumentReader;
import de.jowisoftware.sshclient.application.settings.persistence.xml.XMLDocumentWriter;
import org.apache.log4j.Logger;

import java.io.File;

public class Persister {
    private static final Logger LOGGER = Logger.getLogger(Persister.class);

    private final File settingsFile;

    public Persister(final File settingsFile) {
        this.settingsFile = settingsFile;
    }

    public void persist(final Object object) {
        try {
            final XMLDocumentWriter writer = new XMLDocumentWriter();
            PersistenceAnnotationTraverser.notifySafe(object);
            PersistenceAnnotationTraverser.traverseObject(object, new WriteCallback(writer));
            writer.writeFile(settingsFile);
        } catch(Exception e) {
            LOGGER.error("Could not save application settings", e);
        }
    }

    public void restore(final Object object) {
        try {
            final DocumentReader reader = new XMLDocumentReader(settingsFile);
            PersistenceAnnotationTraverser.traverseObject(object, new ReadCallback(reader));
            PersistenceAnnotationTraverser.notifyLoad(object);
        } catch(Exception e) {
            LOGGER.error("Could not restore application settings", e);
        }
    }
}
