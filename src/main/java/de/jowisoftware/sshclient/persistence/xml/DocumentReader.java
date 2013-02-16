package de.jowisoftware.sshclient.persistence.xml;

public interface DocumentReader {
    XMLDocumentReader.ListReader readList(String path, String name);
    String read(String path);
    DocumentReader readSubNode(String name);
}
