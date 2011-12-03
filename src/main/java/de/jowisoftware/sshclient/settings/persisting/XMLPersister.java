package de.jowisoftware.sshclient.settings.persisting;

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

import de.jowisoftware.sshclient.encryption.PasswordStorage;
import de.jowisoftware.sshclient.settings.AWTProfile;
import de.jowisoftware.sshclient.settings.ApplicationSettings;
import de.jowisoftware.sshclient.settings.ApplicationSettings.TabState;
import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.ui.terminal.AWTGfxInfo;

public class XMLPersister {
    private final ApplicationSettings settings;
    private Document doc;

    public XMLPersister(final ApplicationSettings settings) {
        this.settings = settings;
    }

    public void save(final File file) {
        createDocument();

        final Element root = doc.createElement("config");

        root.appendChild(storeGeneralSettings());
        root.appendChild(storeKeys());
        root.appendChild(storeProfiles());
        root.appendChild(storePasswords());
        doc.appendChild(root);

        saveXMLToFile(file);
    }

    private Node storePasswords() {
        final Element passwords = doc.createElement("passwords");
        final PasswordStorage manager = settings.getPasswordStorage();

        if (manager != null && manager.getCheckString() != null) {
            addCheckAttribute(passwords, manager);
            addPasswords(passwords, manager.exportPasswords());
        }

        return passwords;
    }

    private void addPasswords(final Element passwords,
            final Map<String, String> exportedPasswords) {
        for (final Entry<String, String> password : exportedPasswords.entrySet()) {
            final Element element = doc.createElement("password");
            element.setAttribute("id", password.getKey());
            element.setTextContent(password.getValue());
            passwords.appendChild(element);
        }
    }

    private void addCheckAttribute(final Element passwords, final PasswordStorage manager) {
        passwords.setAttribute("check", manager.getCheckString());
    }

    private Node storeProfiles() {
        final Element profiles = doc.createElement("profiles");

        for (final Entry<String, AWTProfile> profile :
                settings.getProfiles().entrySet()) {
            final Element profileNode = doc.createElement("profile");
            profileNode.setAttribute("name", profile.getKey());
            storeProfile(profile.getValue(), profileNode);
            profiles.appendChild(profileNode);
        }

        return profiles;
    }

    private void storeProfile(final AWTProfile profile, final Element profileNode) {
        profileNode.appendChild(createKeyValue("host", profile.getHost()));
        profileNode.appendChild(createKeyValue("user", profile.getUser()));
        profileNode.appendChild(createKeyValue("port", profile.getPort()));
        profileNode.appendChild(createKeyValue("timeout", profile.getTimeout()));
        profileNode.appendChild(createKeyValue("charset", profile.getCharset().name()));
        profileNode.appendChild(storeEnvironment(profile.getEnvironment()));
        profileNode.appendChild(storeGfxSettings(profile.getGfxSettings()));
    }

    private Node storeEnvironment(final Map<String, String> environment) {
        final Element node = doc.createElement("environment");
        for (final Entry<String, String> entry : environment.entrySet()) {
            final Element child = doc.createElement("variable");
            child.setAttribute("name", entry.getKey());
            child.appendChild(doc.createTextNode(entry.getValue()));
            node.appendChild(child);
        }
        return node;
    }

    private Node storeGfxSettings(final AWTGfxInfo gfxSettings) {
        final Element node = doc.createElement("gfx");
        node.appendChild(createFont(gfxSettings.getFont()));
        node.appendChild(createKeyValue("cursorColor", gfxSettings.getCursorColor()));
        final Element colors = doc.createElement("colors");
        persistColors(gfxSettings.getColorMap(), colors);
        node.appendChild(colors);
        final Element lightColors = doc.createElement("lightColors");
        persistColors(gfxSettings.getLightColorMap(), lightColors);
        node.appendChild(lightColors);
        return node;
    }

    private void persistColors(final Map<ColorName, Color> colorMap,
            final Element parent) {
        for(final Entry<ColorName, Color> e : colorMap.entrySet()) {
            final Element color = doc.createElement("color");
            color.setAttribute("name", e.getKey().name());
            color.setAttribute("value", Integer.toString(e.getValue().getRGB() & 0xFFFFFF, 16));
            parent.appendChild(color);
        }
    }

    private Node createKeyValue(final String key, final Color color) {
        return createKeyValue(key, Integer.toString(color.getRGB() & 0xFFFFFF, 16));
    }

    private Node createKeyValue(final String key, final int value) {
        return createKeyValue(key, Integer.toString(value));
    }

    private Node createFont(final Font font) {
        final Element node = doc.createElement("font");
        node.setAttribute("name", font.getName());
        node.setAttribute("size", Integer.toString(font.getSize()));
        node.setAttribute("style", Integer.toString(font.getStyle()));
        return node;
    }

    private Node createKeyValue(final String key, final String value) {
        final Element node = doc.createElement(key);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

    private Element storeKeys() {
        final Element keyNode = doc.createElement("keys");

        for (final File keyFile : settings.getKeyFiles()) {
            final Element file = doc.createElement("key");
            final Text content = doc.createTextNode(keyFile.getAbsolutePath());
            file.appendChild(content);
            keyNode.appendChild(file);
        }

        return keyNode;
    }

    private Element storeGeneralSettings() {
        final Element element = doc.createElement("settings");

        final Element keyTabState = doc.createElement("keytab");
        tabStateToAttributeValue(settings.getKeyTabState(), keyTabState);
        element.appendChild(keyTabState);

        final Element logTabState = doc.createElement("logtab");
        tabStateToAttributeValue(settings.getLogTabState(), logTabState);
        element.appendChild(logTabState);

        final Element language = doc.createElement("language");
        language.appendChild(doc.createTextNode(settings.getLanguage()));
        element.appendChild(language);

        return element;
    }

    private void tabStateToAttributeValue(final TabState tabState,
            final Element keyTabState) {
        keyTabState.setAttribute("state", tabState.toString().toLowerCase());
    }

    private void saveXMLToFile(final File file)
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

    private void createDocument() {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        doc = docBuilder.newDocument();
    }
}
