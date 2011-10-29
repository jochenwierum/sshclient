package de.jowisoftware.sshclient.terminal.input;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.nio.charset.Charset;

import org.junit.Test;

public class EncodingDecoderTest {
    @Test
    public void testSimpleChars() {
        final EncodingDecoder dec = new EncodingDecoder(Charset.forName("UTF-8"));
        assertEquals('A', dec.nextByte((byte) 65).charValue());
        dec.nextByte((byte)65);
        dec.nextByte((byte)66);
        assertEquals(' ', dec.nextByte((byte) 32).charValue());
    }

    @Test
    public void testMultiByte() {
        final EncodingDecoder dec = new EncodingDecoder(Charset.forName("UTF-8"));
        assertNull(dec.nextByte((byte) -61));
        assertEquals('Ä', dec.nextByte((byte) -124).charValue());
    }
}
