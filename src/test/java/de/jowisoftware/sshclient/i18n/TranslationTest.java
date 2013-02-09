package de.jowisoftware.sshclient.i18n;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.testng.annotations.Test;

public class TranslationTest {
    public Reader toStreamReader(final Properties prop) {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            prop.store(stream, "test");
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return new InputStreamReader(new ByteArrayInputStream(stream.toByteArray()));
    }

    @Test
    public void testDefault() throws IOException {
        final Properties properties = new Properties();
        final Translation t = new Translation(toStreamReader(properties));

        String string = "a test";
        assertEquals(string, t.translate("x", string));

        string = "another test";
        assertEquals(string, t.translate("y", string));
    }

    @Test
    public void testLoadedString() throws IOException {
        final Properties properties = new Properties();
        properties.put("x.new_file", "neu");
        properties.put("x.quit", "beenden");

        final Translation t = new Translation(toStreamReader(properties));

        String toTranslate = "new file";
        String expected = "neu";
        assertEquals(expected, t.translate("x.new_file", toTranslate));

        toTranslate = "Quit";
        expected = "beenden";
        assertEquals(expected, t.translate("x.quit", toTranslate));
    }

    @Test
    public void testFormat() throws IOException {
        final Properties properties = new Properties();
        final String string1 = "There are %d tables";
        final String string2 = "Could not write %s: %s";
        final String string3 = "%d + %d = %s";
        properties.put("tables", "Es gibt %d Tische");
        properties.put("error", "Konnte %s nicht schreiben: %s");

        final Translation t = new Translation(toStreamReader(properties));

        String expected = "Es gibt 10 Tische";
        assertEquals(expected, t.translate("tables", string1, 10));

        expected = "Konnte text.xml nicht schreiben: File not found";
        assertEquals(expected, t.translate("error", string2, "text.xml", "File not found"));

        expected = "21 + 21 = fourtytwo";
        assertEquals(expected, t.translate("x", string3, 21, 21, "fourtytwo"));
    }

    @Test
    public void testNoLanguage() {
        final Translation t = new Translation();
        assertEquals("1 2", t.translate("key", "1 2"));
    }
}
