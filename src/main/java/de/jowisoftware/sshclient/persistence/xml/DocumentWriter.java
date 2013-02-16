package de.jowisoftware.sshclient.persistence.xml;

public interface DocumentWriter {
    XMLDocumentWriter.ListWriter writeList(String path, String name);
    void write(String path, String value);
    DocumentWriter writeSubNode(String name);
}
