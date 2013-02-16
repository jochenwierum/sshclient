package de.jowisoftware.sshclient.persistence.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XMLDocumentReader implements DocumentReader {
    public class ListReader {
        private final NodeList nodeList;
        private int pos = 0;

        public ListReader(final Element list, final String name) {
            if (list == null) {
                nodeList = null;
            } else {
                nodeList = list.getElementsByTagName(name);
            }
        }

        public DocumentReader nextNode() {
            if (nodeList == null || pos == nodeList.getLength()) {
                return null;
            }

            Element element = (Element) nodeList.item(pos);
            ++pos;

            return new ForwardingDocumentReader(element);
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

    private final Document doc;
    private final Element root;

    public XMLDocumentReader(String xml)
            throws SAXException, IOException {
        try (InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"))) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
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
    public String read(String path) {
        return read(path, root);
    }

    private String read(String path, Element start) {
        Element current = start;

        if (current == null) {
            return null;
        }

        String[] segments = path.split("/");
        for (int i = 0; i < segments.length; ++i) {
            final String segment = segments[i];

            if (!segment.isEmpty()) {
                if (segment.startsWith("@") && i < segments.length - 1) {
                    throw new IllegalStateException("Attributes must be the last element");
                }

                if (segment.startsWith("@")) {
                    return current.getAttribute(segment.substring(1));
                } else {
                    NodeList children = current.getElementsByTagName(segment);
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

    private Element findElement(String path, Element start) {
        Element current = start;

        if (current == null) {
            return null;
        }

        String[] segments = path.split("/");
        for (int i = 0; i < segments.length; ++i) {
            final String segment = segments[i];

            if (segment.startsWith("@")) {
                throw new IllegalStateException("Attributes are not allowed");
            }

            NodeList children = current.getElementsByTagName(segment);
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
