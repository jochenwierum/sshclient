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
import de.jowisoftware.sshclient.ui.GfxInfo;

public class XMLLoader {
    private static final Logger LOGGER = Logger.getLogger(XMLLoader.class);
    private final ApplicationSettings settings;

    public XMLLoader(final ApplicationSettings settings) {
        this.settings = settings;
    }

    public void load(final File file) {
        try {
            final Document doc = createDocument(file);
            final XPath xpath = createXPath();

            final Element settingsNode = getElement(xpath, doc, "/config/settings");
            if (settingsNode != null) {
                loadGeneralSettings(xpath, settingsNode);
            }

            final Element keysNode = getElement(xpath, doc, "/config/keys");
            if (keysNode != null) {
                loadKeys(xpath, keysNode);
            }

            final Element profilesNode = getElement(xpath, doc, "/config/profiles");
            if (profilesNode != null) {
                loadProfiles(xpath, profilesNode);
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

    private void loadProfiles(final XPath xpath, final Element profilesNode) {
        final NodeList profileNodes;
        try {
            profileNodes = (NodeList) xpath.evaluate("profile",
                    profilesNode, XPathConstants.NODESET);
        } catch (final XPathExpressionException e) {
            return;
        }

        for (int i = 0; i < profileNodes.getLength(); ++i) {
            final String name = getString(xpath, profileNodes.item(i), "@name",
                    "unknown profile");
            final Profile profile = loadProfile(xpath, profileNodes.item(i));
            settings.getProfiles().put(name, profile);
        }
    }

    private Profile loadProfile(final XPath xpath, final Node profileNode) {
        final Profile profile = new Profile();

        final String host = getString(xpath, profileNode, "host/text()", null);
        final String user = getString(xpath, profileNode, "user/text()", null);
        final Integer port = getInteger(xpath, profileNode, "port/text()", null);
        final Integer timeout = getInteger(xpath, profileNode, "timeout/text()", null);

        Charset charset;
        try {
            charset = Charset.forName(getString(xpath, profileNode,
                    "charset/text()", "UTF-8"));
        } catch(final RuntimeException e) {
            LOGGER.warn("Ignoring unkown charset", e);
            charset = null;
        }

        if (host != null) { profile.setHost(host); }
        if (user != null) { profile.setUser(user); }
        if (port != null) { profile.setPort(port); }
        if (timeout != null) { profile.setTimeout(timeout); }
        if (charset != null) { profile.setCharset(charset); }

        final Element gfxNode = getElement(xpath, profileNode, "gfx");
        if (gfxNode != null) {
            loadGfxSettingsToProfile(xpath, gfxNode, profile.getGfxSettings());
        }

        return profile;
    }

    private void loadGfxSettingsToProfile(final XPath xpath, final Element gfxNode,
            final GfxInfo gfxSettings) {
        gfxSettings.getColorMap().putAll(
                getColors(xpath, gfxNode, "colors/color[@name][@value]"));
        gfxSettings.getLightColorMap().putAll(
                getColors(xpath, gfxNode, "colors/color[@name][@value]"));
        final Color cursorColor = getAWTColor(xpath, gfxNode, "cursorColor/text()");
        if (cursorColor != null) {
            gfxSettings.setCursorColor(cursorColor);
        }
        final Font font = getFont(xpath, gfxNode, "font");
        if (font != null) {
            gfxSettings.setFont(font);
        }
    }

    private Font getFont(final XPath xpath, final Element gfxNode, final String expression) {
        final Element fontNode = getElement(xpath, gfxNode, expression);
        if (fontNode == null) {
            return null;
        }

        final String fontName = getString(xpath, fontNode, "@name", null);
        final Integer fontSize = getInteger(xpath, fontNode, "@size", 11);
        final Integer fontStyle = getInteger(xpath, fontNode, "@size", 0);

        if (fontName != null) {
            return new Font(fontName, fontStyle, fontSize);
        }
        return null;
    }

    private Map<de.jowisoftware.sshclient.terminal.Color, Color> getColors(
            final XPath xpath, final Element node, final String xpathExpression) {
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
            final Color awtColor = getAWTColor(xpath, colorList.item(i), "@value");
            final de.jowisoftware.sshclient.terminal.Color termColor =
                    getTermColor(xpath, colorList.item(i));

            if (awtColor != null && termColor != null) {
                colors.put(termColor, awtColor);
            }
        }

        return colors;
    }

    private Color getAWTColor(final XPath xpath, final Node item, final String expression) {
        final String rgbHexValue = getString(xpath, item, expression, null);
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

    private de.jowisoftware.sshclient.terminal.Color getTermColor(final XPath xpath, final Node item) {
        final String name = getString(xpath, item, "@name", null);
        if (name == null) {
            return null;
        }
        try {
            return de.jowisoftware.sshclient.terminal.Color.valueOf(name.toUpperCase());
        } catch(final IllegalArgumentException e) {
            return null;
        }
    }

    private void loadKeys(final XPath xpath, final Element keysNode) {
        final Boolean loadKeys = getBoolean(xpath, keysNode, "@unlockOnStart", false);
        settings.setUnlockKeyOnStart(loadKeys);

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

    private Boolean getBoolean(final XPath xpath, final Element parent,
            final String path, final Boolean defaultValue) {
        final String value = getString(xpath, parent, path, null);
        if (value != null && value.toLowerCase().equals("false")) {
            return false;
        } else if (value != null && value.toLowerCase().equals("true")) {
            return true;
        } else {
            return defaultValue;
        }
    }

    private void loadGeneralSettings(final XPath xpath, final Element settingsNode) {
        String state = getString(xpath, settingsNode, "keytab/@state", null);
        TabState tabState = restoreTabState(state);
        if (tabState != null) {
            settings.setKeyTabState(tabState);
        }

        state = getString(xpath, settingsNode, "logtab/@state", null);
        tabState = restoreTabState(state);
        if (tabState != null) {
            settings.setLogTabState(tabState);
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

    private Element getElement(final XPath xpath, final Node parent, final String path) {
        try {
            return (Element) xpath.evaluate(path, parent, XPathConstants.NODE);
        } catch (final XPathExpressionException e) {
            return null;
        }
    }

    private String getString(final XPath xpath, final Node parent, final String path, final String defaultValue) {
        try {
            return (String) xpath.evaluate(path, parent, XPathConstants.STRING);
        } catch (final XPathExpressionException e) {
            return defaultValue;
        }
    }

    private Integer getInteger(final XPath xpath, final Node parent, final String path, final Integer defaultValue) {
        final String value = getString(xpath, parent, path, null);
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
