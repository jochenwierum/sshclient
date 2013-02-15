package de.jowisoftware.sshclient.application.settings.persisting;

import java.awt.Color;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.jowisoftware.sshclient.application.settings.ApplicationSettings;
import de.jowisoftware.sshclient.application.settings.Forwarding;
import de.jowisoftware.sshclient.application.settings.TabState;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.encryption.PasswordStorage;
import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.terminal.gfx.awt.AWTGfxInfo;

public class XMLPersister {
    private final ApplicationSettings<AWTProfile> settings;
    private Document doc;

    public XMLPersister(final ApplicationSettings<AWTProfile> settings) {
        this.settings = settings;
    }

    public void save(final File file) {
        createDocument();

        final Element root = doc.createElement("config");

        root.appendChild(createSettingsNode());
        root.appendChild(createKeysNode());
        root.appendChild(createProfilesNode());
        root.appendChild(createPasswordsNode());
        doc.appendChild(root);

        saveXMLToFile(file);
    }

    private Node createPasswordsNode() {
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

    private Node createProfilesNode() {
        final Element profiles = doc.createElement("profiles");

        for (final Entry<String, AWTProfile> profile :
                settings.getProfiles().entrySet()) {
            final Element profileNode = doc.createElement("profile");
            profileNode.setAttribute("name", profile.getKey());
            populateProfileNode(profile.getValue(), profileNode);
            profiles.appendChild(profileNode);
        }

        return profiles;
    }

    private void populateProfileNode(final AWTProfile profile, final Element profileNode) {
        profileNode.appendChild(createKeyValue("host", profile.getHost()));
        profileNode.appendChild(createKeyValue("user", profile.getUser()));
        profileNode.appendChild(createKeyValue("port", profile.getPort()));
        profileNode.appendChild(createKeyValue("timeout", profile.getTimeout()));
        profileNode.appendChild(createKeyValue("keepAliveCount",
                profile.getKeepAliveCount()));
        profileNode.appendChild(createKeyValue("keepAliveInterval",
                profile.getKeepAliveInterval()));
        profileNode.appendChild(createKeyValue("charset", profile.getCharset().name()));
        profileNode.appendChild(createKeyValue("closeTabMode",
                profile.getCloseTabMode().toString()));
        profileNode.appendChild(createKeyValue("command", profile.getCommand()));
        profileNode.appendChild(createEnvironmentNode(profile.getEnvironment()));
        profileNode.appendChild(createGfxNode(profile.getGfxSettings()));
        profileNode.appendChild(createForwardingsNode(profile));
    }

    private Node createForwardingsNode(final AWTProfile profile) {
        final Element node = doc.createElement("forwardings");
        node.appendChild(createKeyValue("forwardX11", profile.getX11Forwarding()));
        node.appendChild(createKeyValue("forwardAgent", profile.getAgentForwarding()));
        node.appendChild(createKeyValue("x11Host", profile.getX11Host()));
        node.appendChild(createKeyValue("x11Display", profile.getX11Display()));
        node.appendChild(createKeyValue("proxyPort",
                profile.getSocksPort() == null ? "" :
                        profile.getSocksPort().toString()));

        node.appendChild(createForwardingNode("portForwardings", profile.getPortForwardings()));
        return node;
    }

    private Node createForwardingNode(final String name,
            final List<Forwarding> forwardings) {
        final Element node = doc.createElement(name);

        for (final Forwarding forwarding : forwardings) {
            final Element forwardingNode = doc.createElement("forwarding");
            forwardingNode.setAttribute("direction", forwarding.direction.longName);
            forwardingNode.setAttribute("sourceHost", forwarding.sourceHost);
            forwardingNode.setAttribute("sourcePort", Integer.toString(forwarding.sourcePort));
            forwardingNode.setAttribute("remoteHost", forwarding.remoteHost);
            forwardingNode.setAttribute("remotePort", Integer.toString(forwarding.remotePort));
            node.appendChild(forwardingNode);
        }

        return node;
    }

    private Node createEnvironmentNode(final Map<String, String> environment) {
        final Element node = doc.createElement("environment");
        for (final Entry<String, String> entry : environment.entrySet()) {
            final Element child = doc.createElement("variable");
            child.setAttribute("name", entry.getKey());
            child.appendChild(doc.createTextNode(entry.getValue()));
            node.appendChild(child);
        }
        return node;
    }

    private Node createGfxNode(final AWTGfxInfo gfxSettings) {
        final Element node = doc.createElement("gfx");
        node.appendChild(createFont(gfxSettings.getFontName(), gfxSettings.getFontSize()));
        node.appendChild(createKeyValue("cursorColor", gfxSettings.getCursorColor()));

        final Element colors = doc.createElement("colors");
        addColorNodes(gfxSettings.getColorMap(), colors);
        node.appendChild(colors);

        final Element lightColors = doc.createElement("lightColors");
        addColorNodes(gfxSettings.getLightColorMap(), lightColors);
        node.appendChild(lightColors);

        final Element antiAliasing = doc.createElement("antiAliasing");
        antiAliasing.setAttribute("type", Integer.toString(gfxSettings.getAntiAliasingMode()));
        node.appendChild(antiAliasing);

        final Element boundaryChars = doc.createElement("boundaryChars");
        boundaryChars.setTextContent(gfxSettings.getBoundaryChars());
        node.appendChild(boundaryChars);

        final Element cursorBlinks = doc.createElement("cursorBlinks");
        cursorBlinks.setTextContent(Boolean.toString(gfxSettings.cursorBlinks()));
        node.appendChild(cursorBlinks);

        final Element cursorStyle = doc.createElement("cursorStyle");
        cursorStyle.setTextContent(Integer.toString(gfxSettings.getCursorStyle().ordinal()));
        node.appendChild(cursorStyle);

        return node;
    }

    private void addColorNodes(final Map<ColorName, Color> colorMap,
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

    private Node createFont(final String fontName, final int fontSize) {
        final Element node = doc.createElement("font");
        node.setAttribute("name", fontName);
        node.setAttribute("size", Integer.toString(fontSize));
        return node;
    }

    private Node createKeyValue(final String key, final String value) {
        final Element node = doc.createElement(key);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

    private Node createKeyValue(final String key, final boolean value) {
        return createKeyValue(key, Boolean.toString(value));
    }

    private Element createKeysNode() {
        final Element keyNode = doc.createElement("keys");

        for (final File keyFile : settings.getKeyFiles()) {
            final Element file = doc.createElement("key");
            final Text content = doc.createTextNode(keyFile.getAbsolutePath());
            file.appendChild(content);
            keyNode.appendChild(file);
        }

        return keyNode;
    }

    private Element createSettingsNode() {
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

        final Element bellType = doc.createElement("bellType");
        bellType.setTextContent(Integer.toString(settings.getBellType().ordinal()));
        element.appendChild(bellType);

        element.appendChild(createDefaultProfileNode());

        return element;
    }

    private Element createDefaultProfileNode() {
        final Element profileNode = doc.createElement("defaultProfile");
        populateProfileNode(settings.newDefaultProfile(), profileNode);
        return profileNode;
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
