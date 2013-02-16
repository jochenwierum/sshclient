package de.jowisoftware.sshclient.application.settings.persisting;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.jowisoftware.sshclient.application.settings.ApplicationSettings;
import de.jowisoftware.sshclient.application.settings.BellType;
import de.jowisoftware.sshclient.application.settings.Forwarding;
import de.jowisoftware.sshclient.application.settings.TabState;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.application.settings.validation.ValidationResult;
import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.terminal.gfx.CursorStyle;
import de.jowisoftware.sshclient.terminal.gfx.awt.AWTGfxInfo;
import de.jowisoftware.sshclient.ui.settings.validation.AWTProfileValidator;
import de.jowisoftware.sshclient.ui.terminal.CloseTabMode;

public class XMLLoader {
    private static final Logger LOGGER = Logger.getLogger(XMLLoader.class);
    private final ApplicationSettings<AWTProfile> settings;
    private XPath xpath;

    public XMLLoader(final ApplicationSettings<AWTProfile> settings) {
        this.settings = settings;
    }

    public void load(final File file) {
        try {
            final Document doc = createDocument(file);
            xpath = createXPath();

            final Element settingsNode = getElement(doc, "/config/settings");
            if (settingsNode != null) {
                loadGeneralSettings(settingsNode);
            }

            final Element keysNode = getElement(doc, "/config/keys");
            if (keysNode != null) {
                loadKeys(keysNode);
            }

            final Element profilesNode = getElement(doc, "/config/profiles");
            if (profilesNode != null) {
                loadProfiles(profilesNode);
            }

            final Element passwordNode = getElement(doc, "/config/passwords");
            if (passwordNode != null) {
                loadPasswords(passwordNode);
            }
        } catch(final Exception e) {
            LOGGER.warn("Unable to load settings file, using default settings", e);
        }
    }

    private Document createDocument(final File file) throws SAXException, IOException {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        return docBuilder.parse(file);
    }


    private XPath createXPath() {
        final XPathFactory factory = XPathFactory.newInstance();
        return factory.newXPath();
    }


    private void loadPasswords(final Element passwordNode) {
        final String checkString = getString(passwordNode, "@check", "");
        if (checkString.isEmpty()) {
            return;
        }

        final Map<String, String> passwords = loadPasswordMap(passwordNode);

        settings.getPasswordStorage().setCheckString(checkString);
        settings.getPasswordStorage().importPasswords(passwords);
    }

    private Map<String, String> loadPasswordMap(final Element passwordNode) {
        final Map<String, String> passwords = new HashMap<String, String>();
        NodeList passwordNodes;

        try {
            passwordNodes = (NodeList) xpath.evaluate("password",
                    passwordNode, XPathConstants.NODESET);
        } catch (final XPathExpressionException e) {
            return passwords;
        }

        for (int i = 0; i < passwordNodes.getLength(); ++i) {
            final String id = getString(passwordNodes.item(i), "@id", null);
            final String password = getString(passwordNodes.item(i), "text()", null);

            if (id != null && password != null) {
                passwords.put(id, password);
            }
        }

        return passwords;
    }

    private void loadProfiles(final Element profilesNode) {
        final NodeList profileNodes;
        try {
            profileNodes = (NodeList) xpath.evaluate("profile",
                    profilesNode, XPathConstants.NODESET);
        } catch (final XPathExpressionException e) {
            LOGGER.warn("Could not find profile tag", e);
            return;
        }

        for (int i = 0; i < profileNodes.getLength(); ++i) {
            final String name = getString(profileNodes.item(i), "@name",
                    "unknown profile");
            final AWTProfile profile = loadProfile(profileNodes.item(i));
            if (profileIsValid(profile)) {
                settings.getProfiles().put(name, profile);
            }
        }
    }

    private boolean profileIsValid(final AWTProfile profile) {
        final ValidationResult result = new AWTProfileValidator(profile).validateProfile();
        if (result.hadErrors()) {
            LOGGER.warn("Ignoring profile " + profile + ", profile was invalid");
            return false;
        }
        return true;
    }

    private AWTProfile loadProfile(final Node profileNode) {
        final AWTProfile profile = new AWTProfile();

        profile.setHost(getString(profileNode, "host/text()", null));
        profile.setUser(getString(profileNode, "user/text()", null));
        profile.setPort(getInteger(profileNode, "port/text()", -1));
        profile.setTimeout(getInteger(profileNode, "timeout/text()", -1));
        profile.setKeepAliveCount(getInteger(profileNode,
                "keepAliveCount/text()", profile.getKeepAliveCount()));
        profile.setKeepAliveInterval(getInteger(profileNode,
                "keepAliveInterval/text()", profile.getKeepAliveInterval()));
        profile.setCommand(getString(profileNode, "command/text()", ""));

        restoreForwardings(profile, profileNode);

        restoreCharset(profileNode, profile);
        restoreCloseTaMode(profileNode, profile);

        final Element environmentNode = getElement(profileNode, "environment");
        if (environmentNode != null) {
            loadEnvironment(environmentNode, profile.getEnvironment());
        }

        final Element gfxNode = getElement(profileNode, "gfx");
        if (gfxNode != null) {
            loadGfxSettingsToProfile(gfxNode, profile.getGfxSettings());
        }

        return profile;
    }

    private void restoreCharset(final Node profileNode, final AWTProfile profile) {
        final String charset = getString(profileNode,
                    "charset/text()", "UTF-8");
        profile.setCharsetName(charset);
    }

    private void restoreCloseTaMode(final Node profileNode,
            final AWTProfile profile) {
        final String closeTabMode = getString(profileNode, "closeTabMode/text()", null);
        if (closeTabMode != null) {
            final String enumValue = closeTabMode.toUpperCase();
            try {
                 profile.setCloseTabMode(CloseTabMode.valueOf(enumValue));
            } catch(final Exception e) {
                LOGGER.warn("unable to find CloseTabMode: " + enumValue, e);
            }
        }
    }

    private void restoreForwardings(final AWTProfile profile, final Node profileNode) {
        profile.setAgentForwarding(getBoolean(profileNode, "forwardings/forwardAgent/text()",
                profile.getAgentForwarding()));
        profile.setX11Forwarding(getBoolean(profileNode, "forwardings/forwardX11/text()",
                profile.getX11Forwarding()));
        profile.setX11Host(getString(profileNode, "forwardings/x11Host/text()",
                profile.getX11Host()));
        profile.setX11Display(getInteger(profileNode, "forwardings/x11Display/text()",
                profile.getX11Display()));
        profile.setSocksPort(getInteger(profileNode,
                "forwardings/proxyPort/text()", profile.getSocksPort()));

        final Element portForwardings = getElement(profileNode, "forwardings/portForwardings");
        if (portForwardings != null) {
            restoreForwardings(portForwardings, profile.getPortForwardings());
        }
    }

    private void restoreForwardings(final Element forwardingsNode,
            final List<Forwarding> forwardings) {

        final NodeList forwardingNodes = forwardingsNode.getChildNodes();
        for (int i = 0; i < forwardingNodes.getLength(); ++i) {
            final Node forwardingNode = forwardingNodes.item(i);
            if (forwardingNode.getNodeType() == Node.ELEMENT_NODE) {
                final Element forwardingElement = (Element) forwardingNode;

                final Forwarding forwarding = getForwarding(forwardingElement);
                if (forwarding != null) {
                    forwardings.add(forwarding);
                }
            }
        }
    }

    private Forwarding getForwarding(final Element forwardingElement) {
        final String directionString = getString(forwardingElement, "@direction", null);
        final Integer remotePort = getInteger(forwardingElement, "@remotePort", null);
        final Integer sourcePort = getInteger(forwardingElement, "@sourcePort", null);
        final String remoteHost = getString(forwardingElement, "@remoteHost", null);
        final String sourceHost = getString(forwardingElement, "@sourceHost", null);

        Forwarding.Direction direction = null;
        for (final Forwarding.Direction possibleDirection : Forwarding.Direction.values()) {
            if (possibleDirection.longName.equals(directionString)) {
                direction = possibleDirection;
            }
        }

        if (direction != null
                && remotePort != null && sourcePort != null
                && remoteHost != null && sourceHost != null) {
            return new Forwarding(direction, sourceHost, sourcePort, remoteHost, remotePort);
        } else {
            return null;
        }
    }

    private void loadEnvironment(final Element environmentNode,
            final Map<String, String> environment) {
        final NodeList nodes = environmentNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            final String key = getString(node, "./@name", null);
            final String value = getString(node, "./text()", null);
            if (key != null && value != null) {
                environment.put(key, value);
            }
        }
    }

    private void loadGfxSettingsToProfile(final Element gfxNode,
            final AWTGfxInfo gfxSettings) {
        gfxSettings.getColorMap().putAll(
                getColors(gfxNode, "colors/color[@name][@value]"));
        gfxSettings.getLightColorMap().putAll(
                getColors(gfxNode, "lightColors/color[@name][@value]"));
        final Color cursorColor = getAWTColor(gfxNode, "cursorColor/text()");
        if (cursorColor != null) {
            gfxSettings.setCursorColor(cursorColor);
        }
        applyFont(gfxNode, "font", gfxSettings);

        final Integer antiAliasing = getInteger(gfxNode, "antiAliasing/@type", null);
        if (antiAliasing != null) {
            gfxSettings.setAntiAliasingMode(antiAliasing);
        }

        final String boundaryChars = getString(gfxNode, "boundaryChars/text()", null);
        if (boundaryChars != null) {
            gfxSettings.setBoundaryChars(boundaryChars);
        }

        final boolean cursorBlinks = getBoolean(gfxNode, "cursorBlinks/text()", true);
        gfxSettings.setCursorBlinks(cursorBlinks);

        final Integer cursorStyle = getInteger(gfxNode, "cursorStyle/text()", null);
        if (cursorStyle != null) {
            gfxSettings.setCursorStyle(CursorStyle.values()[cursorStyle]);
        }
    }

    private void applyFont(final Element gfxNode, final String expression,
            final AWTGfxInfo gfxSettings) {
        final Element fontNode = getElement(gfxNode, expression);
        if (fontNode == null) {
            return;
        }

        final String fontName = getString(fontNode, "@name", Font.MONOSPACED);
        final Integer fontSize = getInteger(fontNode, "@size", 10);

        gfxSettings.setFontName(fontName);
        gfxSettings.setFontSize(fontSize);
    }

    private Map<ColorName, Color> getColors(
            final Element node, final String xpathExpression) {
        final NodeList colorList;
        try {
            colorList = (NodeList) xpath.evaluate(xpathExpression,
                    node, XPathConstants.NODESET);
        } catch (final XPathExpressionException e) {
            return null;
        }

        final Map<ColorName, Color> colors  =
                new HashMap<ColorName, Color>();
        for (int i = 0; i < colorList.getLength(); ++i) {
            final Color awtColor = getAWTColor(colorList.item(i), "@value");
            final ColorName termColor =
                    getTermColor(colorList.item(i));

            if (awtColor != null && termColor != null) {
                colors.put(termColor, awtColor);
            }
        }

        return colors;
    }

    private Color getAWTColor(final Node item, final String expression) {
        final String rgbHexValue = getString(item, expression, null);
        if (rgbHexValue == null) {
            return null;
        }

        final int rgbValue;
        try {
            rgbValue = Integer.parseInt(rgbHexValue, 16);
        } catch (final NumberFormatException e) {
            LOGGER.warn("Illegal color code, igonring: " + rgbHexValue, e);
            return null;
        }

        return new Color(rgbValue | 0xFF000000);
    }

    private ColorName getTermColor(final Node item) {
        final String name = getString(item, "@name", null);
        if (name == null) {
            return null;
        }
        try {
            return ColorName.valueOf(name.toUpperCase());
        } catch(final IllegalArgumentException e) {
            return null;
        }
    }

    private void loadKeys(final Element keysNode) {
        try {
            final NodeList nodes = (NodeList) xpath.evaluate("key/text()", keysNode, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); ++i) {
                final String keyFile = nodes.item(i).getNodeValue();
                settings.getKeyFiles().add(new File(keyFile));
            }
        } catch (final XPathExpressionException e) {
            return;
        }
    }

    private void loadGeneralSettings(final Element settingsNode) {
        String state = getString(settingsNode, "keytab/@state", null);
        TabState tabState = restoreTabState(state);
        if (tabState != null) {
            settings.setKeyTabState(tabState);
        }

        state = getString(settingsNode, "logtab/@state", null);
        tabState = restoreTabState(state);
        if (tabState != null) {
            settings.setLogTabState(tabState);
        }

        final String language = getString(settingsNode, "language/text()", "en_US");
        settings.setLanguage(language);

        final int bellType = getInteger(settingsNode, "bellType/text()", 1);
        settings.setBellType(BellType.values()[bellType]);

        final Element defaultProfileNode = getElement(settingsNode, "defaultProfile");
        if (defaultProfileNode != null) {
            settings.setDefaultProfile(loadProfile(defaultProfileNode));
        }
    }


    private TabState restoreTabState(final String textContent) {
        if (textContent == null) {
            return null;
        }

        final String enumValue = textContent.toUpperCase();
        try {
            return TabState.valueOf(enumValue);
        } catch(final Exception e) {
            LOGGER.warn("unable to find TabState: " + enumValue, e);
            return null;
        }
    }

    private Element getElement(final Node parent, final String path) {
        try {
            return (Element) xpath.evaluate(path, parent, XPathConstants.NODE);
        } catch (final XPathExpressionException e) {
            return null;
        }
    }

    private String getString(final Node parent, final String path, final String defaultValue) {
        try {
            final Node result = (Node) xpath.evaluate(path, parent, XPathConstants.NODE);
            if (result == null) {
                return defaultValue;
            } else {
                return result.getTextContent();
            }
        } catch (final XPathExpressionException e) {
            return defaultValue;
        }
    }

    private Integer getInteger(final Node parent, final String path, final Integer defaultValue) {
        final String value = getString(parent, path, null);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch(final NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private boolean getBoolean(final Node parent, final String path, final boolean defaultValue) {
        final String value = getString(parent, path, null);
        if (value != null) {
            try {
                return Boolean.parseBoolean(value);
            } catch(final NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
