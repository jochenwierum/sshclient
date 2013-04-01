package de.jowisoftware.sshclient.application.settings.persistence.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class XMLDocumentReader implements DocumentReader {
    public class ListReader {
        private final NodeList nodeList;
        private int pos = 0;

        private ListReader(final Element list, final String name) {
            if (list == null) {
                nodeList = null;
            } else {
                nodeList = list.getChildNodes();
            }
        }

        public DocumentReader nextNode() {
            if (nodeList == null) {
                return null;
            }

            do {
                ++pos;
            } while(pos < nodeList.getLength() && nodeList.item(pos).getNodeType() != Node.ELEMENT_NODE);

            if (pos >= nodeList.getLength()) {
                return null;
            }

            return new ForwardingDocumentReader((Element) nodeList.item(pos));
        }
    }

    private class ForwardingDocumentReader implements DocumentReader {
        private final Element node;

        public ForwardingDocumentReader(final Element node) {
            this.node = node;
        }

        @Override
        public ListReader readList(final String path, final String name) {
            return XMLDocumentReader.this.readList(path, name, node);
        }

        @Override
        public String read(final String path) {
            return XMLDocumentReader.this.read(path, node);
        }

        @Override
        public DocumentReader readSubNode(final String name) {
            return XMLDocumentReader.this.readSubNode(name, node);
        }
    }

    private final Element root;

    public XMLDocumentReader(final File settingsFile) throws SAXException, IOException {
        try (InputStream is = new FileInputStream(settingsFile)) {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.parse(is);
            root = (Element) doc.getChildNodes().item(0);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ListReader readList(final String path, final String name) {
        return readList(path, name, root);
    }

    private ListReader readList(final String path, final String name, final Element node) {
        final Element list = findElement(path, node);
        return new ListReader(list, name);
    }

    @Override
    public String read(final String path) {
        return read(path, root);
    }

    private String read(final String path, final Element start) {
        Element current = start;

        if (current == null) {
            return null;
        }

        final String[] segments = path.split("/");
        for (int i = 0; i < segments.length; ++i) {
            final String segment = segments[i];

            if (!segment.isEmpty()) {
                if (segment.startsWith("@") && i < segments.length - 1) {
                    throw new IllegalStateException("Attributes must be the last element");
                }

                if (segment.startsWith("@")) {
                    return current.getAttribute(segment.substring(1));
                } else {
                    final NodeList children = current.getElementsByTagName(segment);
                    if (children.getLength() == 0) {
                        return null;
                    } else {
                        current = (Element) children.item(0);
                    }
                }
            }
        }

        return current.getTextContent();
    }

    private Element findElement(final String path, final Element start) {
        Element current = start;

        if (current == null) {
            return null;
        }

        final String[] segments = path.split("/");
        for (final String segment : segments) {
            if (segment.startsWith("@")) {
                throw new IllegalStateException("Attributes are not allowed");
            }

            final NodeList children = current.getElementsByTagName(segment);
            if (children.getLength() == 0) {
                return null;
            } else {
                current = (Element) children.item(0);
            }
        }

        return current;
    }

    @Override
    public DocumentReader readSubNode(final String name) {
        return readSubNode(name, root);
    }

    private DocumentReader readSubNode(final String name, final Element node) {
        final Element subNode = findElement(name, node);
        return new ForwardingDocumentReader(subNode);
    }
}
