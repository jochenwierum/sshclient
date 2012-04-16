package de.jowisoftware.sshclient.i18n;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class Translation {
    private static final Logger LOGGER = Logger.getLogger(Translation.class);
    private static final Object NEUTRAL_LANGUAGE = "en_US";
    private final Properties translations;

    private static Translation staticTranslation = new Translation();

    Translation() {
        this.translations = null;
    }

    public Translation(final Reader languageFileReader) throws IOException {
        final Properties properties = new Properties();
        properties.load(languageFileReader);
        this.translations = properties;
    }

    public String translate(final String key, final String string, final Object ... args) {
        String formatString = string;
        if (translations != null) {
            final String result = translations.getProperty(key.toLowerCase());
            if (result != null) {
                formatString = result;
            } else {
                LOGGER.warn("Missing translation: " + key.toLowerCase() + " (\"" + string + "\")");
            }
        }
        return String.format(formatString, args);
    }

    public static void initStaticTranslationWithLanguage(final String language) {
        LOGGER.info("Initializing language: " + language);

        staticTranslation = new Translation();
        if (language != null && !language.equals(NEUTRAL_LANGUAGE)) {
            final String languageFileName = "/lang/" + language + ".properties";
            final InputStream stream = Translation.class.getResourceAsStream(languageFileName);
            if (stream != null) {
                try {
                    staticTranslation = new Translation(new InputStreamReader(stream, Charset.forName("UTF-8")));
                    IOUtils.closeQuietly(stream);
                } catch (final IOException e) {
                    LOGGER.error("Could not read language file: " + language, e);
                }
            } else {
                LOGGER.error("Could not find language: " + language);
            }
        }
    }

    public static String t(final String key, final String string, final Object ... args) {
        return staticTranslation.translate(key, string, args);
    }

    public static int m(final String key, final char defaultKey) {
        final String charString = staticTranslation.translate("mnemonic." + key,
                Character.toString(defaultKey));

        if (charString.equals(key)) {
            return KeyEvent.getExtendedKeyCodeForChar(defaultKey);
        } else if (charString.length() == 1) {
            return KeyEvent.getExtendedKeyCodeForChar(charString.charAt(0));
        } else {
            LOGGER.warn("Illegal key for mnemonic " + key + ": " + charString);
            return KeyEvent.getExtendedKeyCodeForChar(defaultKey);
        }
    }
}
