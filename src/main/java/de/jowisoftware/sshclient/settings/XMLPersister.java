package de.jowisoftware.sshclient.settings;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.jowisoftware.sshclient.settings.ApplicationSettings.TabState;
import de.jowisoftware.sshclient.ui.GfxInfo;

public class XMLPersister {
    private final ApplicationSettings settings;

    public XMLPersister(final ApplicationSettings settings) {
        this.settings = settings;
    }

    public void save(final File file) {
        final Document doc = createDocument();

        final Element root = doc.createElement("config");

        root.appendChild(storeGeneralSettings(doc));
        root.appendChild(storeKeys(doc));
        root.appendChild(storeProfiles(doc));
        doc.appendChild(root);

        saveXMLToFile(file, doc);
    }

    private Node storeProfiles(final Document doc) {
        final Element profiles = doc.createElement("profiles");

        for (final Entry<String, Profile> profile :
                settings.getProfiles().entrySet()) {
            final Element profileNode = doc.createElement("profile");
            profileNode.setAttribute("name", profile.getKey());
            storeProfile(profile.getValue(), profileNode, doc);
            profiles.appendChild(profileNode);
        }

        return profiles;
    }

    private void storeProfile(final Profile profile, final Element profileNode, final Document doc) {
        profileNode.appendChild(createKeyValue(doc, "host", profile.getHost()));
        profileNode.appendChild(createKeyValue(doc, "user", profile.getUser()));
        profileNode.appendChild(createKeyValue(doc, "port", profile.getPort()));
        profileNode.appendChild(createKeyValue(doc, "timeout", profile.getTimeout()));
        profileNode.appendChild(createKeyValue(doc, "charset", profile.getCharset().name()));
        profileNode.appendChild(storeGfxSettings(profile.getGfxSettings(), doc));
    }

    private Node storeGfxSettings(final GfxInfo gfxSettings, final Document doc) {
        final Element node = doc.createElement("gfx");
        node.appendChild(createFont(gfxSettings.getFont(), doc));
        node.appendChild(createKeyValue(doc, "cursorColor", gfxSettings.getCursorColor()));
        final Element colors = doc.createElement("colors");
        persistColors(gfxSettings.getColorMap(), colors, doc);
        node.appendChild(colors);
        final Element lightColors = doc.createElement("lightColors");
        persistColors(gfxSettings.getLightColorMap(), lightColors, doc);
        node.appendChild(lightColors);
        return node;
    }

    private void persistColors(final Map<de.jowisoftware.sshclient.terminal.Color, Color> colorMap,
            final Element parent, final Document doc) {
        for(final Entry<de.jowisoftware.sshclient.terminal.Color, Color> e : colorMap.entrySet()) {
            final Element color = doc.createElement("color");
            color.setAttribute("name", e.getKey().name());
            color.setAttribute("value", Integer.toString(e.getValue().getRGB() & 0xFFFFFF, 16));
            parent.appendChild(color);
        }
    }

    private Node createKeyValue(final Document doc, final String key, final Color color) {
        return createKeyValue(doc, key, Integer.toString(color.getRGB() & 0xFFFFFF, 16));
    }

    private Node createKeyValue(final Document doc, final String key, final int value) {
        return createKeyValue(doc, key, Integer.toString(value));
    }

    private Node createFont(final Font font, final Document doc) {
        final Element node = doc.createElement("font");
        node.setAttribute("name", font.getName());
        node.setAttribute("size", Integer.toString(font.getSize()));
        node.setAttribute("style", Integer.toString(font.getStyle()));
        return node;
    }

    private Node createKeyValue(final Document doc, final String key, final String value) {
        final Element node = doc.createElement(key);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

    private Element storeKeys(final Document doc) {
        final Element keyNode = doc.createElement("keys");

        for (final File keyFile : settings.getKeyFiles()) {
            final Element file = doc.createElement("key");
            final Text content = doc.createTextNode(keyFile.getAbsolutePath());
            file.appendChild(content);
            keyNode.appendChild(file);
        }

        return keyNode;
    }

    private Element storeGeneralSettings(final Document doc) {
        final Element element = doc.createElement("settings");

        final Element keyTabState = doc.createElement("keytab");
        tabStateToAttributeValue(settings.getKeyTabState(), keyTabState);
        element.appendChild(keyTabState);

        final Element logTabState = doc.createElement("logtab");
        tabStateToAttributeValue(settings.getLogTabState(), logTabState);
        element.appendChild(logTabState);

        return element;
    }

    private void tabStateToAttributeValue(final TabState tabState,
            final Element keyTabState) {
        keyTabState.setAttribute("state", tabState.toString().toLowerCase());
    }

    private void saveXMLToFile(final File file, final Document doc)
            throws TransformerFactoryConfigurationError {
        try {
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            final DOMSource source = new DOMSource(doc);
            final StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch (final TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (final TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private Document createDocument() {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        final Document doc = docBuilder.newDocument();
        return doc;
    }
}
