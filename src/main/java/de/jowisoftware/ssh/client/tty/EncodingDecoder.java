package de.jowisoftware.ssh.client.tty;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import org.apache.log4j.Logger;

public class EncodingDecoder {
    private static final Logger LOGGER = Logger.getLogger(EncodingDecoder.class);

    private final ByteBuffer byteBuffer = ByteBuffer.allocate(16);

    private final CharsetDecoder decoder;
    private final float maxBytes;

    public EncodingDecoder(final Charset charset) {
        this.decoder = charset.newDecoder();
        maxBytes = 1 / decoder.maxCharsPerByte();
        decoder.onMalformedInput(CodingErrorAction.REPORT);
        byteBuffer.clear();
    }

    public Character nextByte(final byte value) {
        byteBuffer.put(value);
        final CharBuffer out = CharBuffer.allocate(2);

        final ByteBuffer toDec = convert(out);

        if (toDec.remaining() != 0) {
            checkErrorState();
            return null;
        } else {
            byteBuffer.clear();
            return out.get();
        }
    }

    private void checkErrorState() {
        if (byteBuffer.position() > maxBytes) {
            LOGGER.error("Could not decode: " + byteBuffer.toString());
            byteBuffer.clear();
        }
    }

    private ByteBuffer convert(final CharBuffer out) {
        final ByteBuffer toDec = byteBuffer.duplicate();
        toDec.flip();
        decoder.decode(toDec, out, false);
        out.flip();
        return toDec;
    }
}
