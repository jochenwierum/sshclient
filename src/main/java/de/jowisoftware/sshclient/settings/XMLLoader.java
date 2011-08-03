package de.jowisoftware.sshclient.settings;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
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

import de.jowisoftware.sshclient.settings.ApplicationSettings.TabState;
import de.jowisoftware.sshclient.settings.validation.ValidationResult;
import de.jowisoftware.sshclient.ui.GfxInfo;
import de.jowisoftware.sshclient.util.ValidationUtils;

public class XMLLoader {
    private static final Logger LOGGER = Logger.getLogger(XMLLoader.class);
    private final ApplicationSettings settings;
    private XPath xpath;

    public XMLLoader(final ApplicationSettings settings) {
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

    private void loadProfiles(final Element profilesNode) {
        final NodeList profileNodes;
        try {
            profileNodes = (NodeList) xpath.evaluate("profile",
                    profilesNode, XPathConstants.NODESET);
        } catch (final XPathExpressionException e) {
            return;
        }

        for (int i = 0; i < profileNodes.getLength(); ++i) {
            final String name = getString(profileNodes.item(i), "@name",
                    "unknown profile");
            final Profile profile = loadProfile(profileNodes.item(i));
            if (profileIsValid(profile)) {
                settings.getProfiles().put(name, profile);
            }
        }
    }

    private boolean profileIsValid(final Profile profile) {
        final ValidationResult result = ValidationUtils.validateProfile(profile);
        if (result.hadErrors()) {
            LOGGER.warn("Ignoring profile " + profile + ", profile was invalid");
            return false;
        }
        return true;
    }

    private Profile loadProfile(final Node profileNode) {
        final Profile profile = new Profile();

        profile.setHost(getString(profileNode, "host/text()", null));
        profile.setUser(getString(profileNode, "user/text()", null));
        profile.setPort(getInteger(profileNode, "port/text()", -1));
        profile.setTimeout(getInteger(profileNode, "timeout/text()", -1));

        Charset charset;
        try {
            charset = Charset.forName(getString(profileNode,
                    "charset/text()", "UTF-8"));
        } catch(final RuntimeException e) {
            LOGGER.warn("Ignoring unkown charset", e);
            charset = null;
        }
        profile.setCharset(charset);

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
            final GfxInfo gfxSettings) {
        gfxSettings.getColorMap().putAll(
                getColors(gfxNode, "colors/color[@name][@value]"));
        gfxSettings.getLightColorMap().putAll(
                getColors(gfxNode, "colors/color[@name][@value]"));
        final Color cursorColor = getAWTColor(gfxNode, "cursorColor/text()");
        if (cursorColor != null) {
            gfxSettings.setCursorColor(cursorColor);
        }
        final Font font = getFont(gfxNode, "font");
        if (font != null) {
            gfxSettings.setFont(font);
        }
    }

    private Font getFont(final Element gfxNode, final String expression) {
        final Element fontNode = getElement(gfxNode, expression);
        if (fontNode == null) {
            return null;
        }

        final String fontName = getString(fontNode, "@name", null);
        final Integer fontSize = getInteger(fontNode, "@size", 11);
        final Integer fontStyle = getInteger(fontNode, "@size", 0);

        if (fontName != null) {
            return new Font(fontName, fontStyle, fontSize);
        }
        return null;
    }

    private Map<de.jowisoftware.sshclient.terminal.Color, Color> getColors(
            final Element node, final String xpathExpression) {
        final NodeList colorList;
        try {
            colorList = (NodeList) xpath.evaluate(xpathExpression,
                    node, XPathConstants.NODESET);
        } catch (final XPathExpressionException e) {
            return null;
        }

        final Map<de.jowisoftware.sshclient.terminal.Color, Color> colors  =
                new HashMap<de.jowisoftware.sshclient.terminal.Color, Color>();
        for (int i = 0; i < colorList.getLength(); ++i) {
            final Color awtColor = getAWTColor(colorList.item(i), "@value");
            final de.jowisoftware.sshclient.terminal.Color termColor =
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

    private de.jowisoftware.sshclient.terminal.Color getTermColor(final Node item) {
        final String name = getString(item, "@name", null);
        if (name == null) {
            return null;
        }
        try {
            return de.jowisoftware.sshclient.terminal.Color.valueOf(name.toUpperCase());
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
}
