package de.jowisoftware.sshclient.terminal.input;

import java.nio.charset.Charset;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class CharsetByteProcessorTest {
    private final Mockery context = new JUnit4Mockery();
    private CharacterProcessor callback;

    @Before
    public void setUpMock() {
        callback = context.mock(CharacterProcessor.class);
    }

    private void assertChar(final char ... characters) {
        context.checking(new Expectations() {{
            for (final char character : characters) {
                oneOf(callback).processChar(character);
            }
        }});
    }

    @Test
    public void testSimpleCharsAreForwarded() {
        assertChar('A', 'B', 'C', ' ');
        final ByteProcessor dec = new CharsetByteProcessor(callback,
                Charset.forName("UTF-8"));
        dec.processByte((byte) 65);
        dec.processByte((byte) 66);
        dec.processByte((byte) 67);
        dec.processByte((byte) 32);
    }

    @Test
    public void multiBytesAreCached() {
        final ByteProcessor dec = new CharsetByteProcessor(callback,
                Charset.forName("UTF-8"));
        dec.processByte((byte) -61);
        assertChar('Ä');
        dec.processByte((byte) -124);

        assertChar("𤭢".toCharArray());
        dec.processByte((byte) 0xF0);
        dec.processByte((byte) 0xA4);
        dec.processByte((byte) 0xAD);
        dec.processByte((byte) 0xA2);
    }

    @Test
    public void latinIsProcessed1() {
        final ByteProcessor dec = new CharsetByteProcessor(callback,
                Charset.forName("ISO-8859-1"));
        assertChar('Ä');
        dec.processByte((byte) 0xC4);
    }

    @Test
    public void skipBrokenBytes() {
        final ByteProcessor dec = new CharsetByteProcessor(callback,
                Charset.forName("UTF-8"));

        assertChar('�');
        dec.processByte((byte) 0xFF);

        dec.processByte((byte) 0xC4);
        assertChar('�');
        assertChar('A');
        dec.processByte((byte) 65);
    }
}