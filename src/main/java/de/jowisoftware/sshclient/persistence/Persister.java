package de.jowisoftware.sshclient.persistence;

import de.jowisoftware.sshclient.persistence.annotations.PersistenceAnnotationTraverser;
import de.jowisoftware.sshclient.persistence.xml.DocumentReader;
import de.jowisoftware.sshclient.persistence.xml.XMLDocumentReader;
import de.jowisoftware.sshclient.persistence.xml.XMLDocumentWriter;
import org.xml.sax.SAXException;

import java.io.IOException;

public class Persister {
    public String persist(final Object object) {
        final XMLDocumentWriter writer = new XMLDocumentWriter();
        PersistenceAnnotationTraverser.traverseObject(object, new WriteCallback(writer));
        return writer.toXML();
    }

    public void restore(final String input, final Object object) throws IOException, SAXException {
        final DocumentReader reader = new XMLDocumentReader(input);
        PersistenceAnnotationTraverser.traverseObject(object, new ReadCallback(reader));
    }
}
