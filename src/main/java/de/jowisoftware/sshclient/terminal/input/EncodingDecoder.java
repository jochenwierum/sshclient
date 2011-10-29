package de.jowisoftware.sshclient.terminal.input;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.util.StringUtils;

public class EncodingDecoder {
    private static final Logger LOGGER = Logger.getLogger(EncodingDecoder.class);

    private final ByteBuffer byteBuffer = ByteBuffer.allocate(32);

    private final CharsetDecoder decoder;
    private final float maxBytes;

    public EncodingDecoder(final Charset charset) {
        this.decoder = charset.newDecoder();
        maxBytes = 2 / decoder.maxCharsPerByte();
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
            final StringBuilder bytes = new StringBuilder();
            final StringBuilder chars = new StringBuilder();
            for (int i = 0; i < byteBuffer.position(); ++i) {
                bytes.append(StringUtils.byteToHex(byteBuffer.get(i)));
                chars.append((char) byteBuffer.get(i));
            }
            LOGGER.error("Could not decode: " + bytes.toString() + ": " + chars.toString());
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
