package de.jowisoftware.sshclient.persistence.xml;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class XMLDocumentWriter implements DocumentWriter {
    public class ListWriter {
        private final Element node;
        private final String name;

        private ListWriter(Element node, String name) {
            this.node = node;
            this.name = name;
        }

        public DocumentWriter add() {
            final Element newChild = doc.createElement(name);
            node.appendChild(newChild);
            return new ForwardingDocumentWriter(newChild);
        }
    }
    
    private class ForwardingDocumentWriter implements DocumentWriter {
        private final Element node;

        public ForwardingDocumentWriter(final Element node) {
            this.node = node;
        }

        @Override
        public ListWriter writeList(final String path, final String name) {
            return XMLDocumentWriter.this.addList(path, name, node);
        }

        @Override
        public void write(final String path, final String value) {
            XMLDocumentWriter.this.set(path, value, node);
        }

        @Override
        public DocumentWriter writeSubNode(final String name) {
            return XMLDocumentWriter.this.createSubNode(name, node);
        }
    }

    private final Document doc = createDocument();
    private final Element root = createRoot(doc, "settings");

    private Document createDocument() {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        return docBuilder.newDocument();
    }

    private Element createRoot(Document document, String rootName) {
        Element rootElement = document.createElement(rootName);
        doc.appendChild(rootElement);
        return rootElement;
    }

    private Element findOrCreateNode(Element node, String childName) {
        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            if (children.item(i).getNodeName().equals(childName)) {
                return (Element) children.item(i);
            }
        }

        final Element result = doc.createElement(childName);
        node.appendChild(result);
        return result;
    }

    @Override
    public ListWriter writeList(final String path, final String name) {
        return addList(path, name, root);
    }

    private ListWriter addList(final String path, final String name, final Element start) {
        return new ListWriter(createOrFindElementPath(path, start), name);
    }

    @Override
    public void write(final String path, final String value) {
        set(path, value, root);
    }

    private void set(final String path, final String value, final Element start) {
        Element current = start;
        final String segments[] = path.split("/");

        for (int i = 0; i < segments.length; ++i) {
            final String segment = segments[i];

            if (i < segments.length - 1 && segment.startsWith("@")) {
                throw new IllegalStateException("Attribute must be the last segment");
            }

            if (segment.startsWith("@")) {
                current.setAttribute(segment.substring(1), value);
            } else {
                if (!segment.isEmpty()) {
                    current = findOrCreateNode(current, segment);
                }

                if (i == segments.length - 1) {
                    current.setTextContent(value);
                }
            }
        }
    }

    @Override
    public DocumentWriter writeSubNode(String path) {
        return createSubNode(path, root);
    }

    public DocumentWriter createSubNode(String path, Element start) {
        final Element node = createOrFindElementPath(path, start);
        return new ForwardingDocumentWriter(node);
    }

    private Element createOrFindElementPath(String path, Element start) {
        Element current = start;
        final String segments[] = path.split("/");

        for (int i = 0; i < segments.length; ++i) {
            final String segment = segments[i];

            if (segment.startsWith("@")) {
                throw new IllegalStateException("List must not be saved in Attributes");
            } else {
                current = findOrCreateNode(current, segment);
            }
        }

        return current;
    }

    public String toXML() {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();

            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            transformer.transform(domSource, result);
            writer.flush();
            return writer.toString();
        } catch(TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
