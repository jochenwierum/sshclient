package de.jowisoftware.sshclient.terminal.input;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.util.StringUtils;

public class CharsetByteProcessor implements ByteProcessor {
    enum DecodeResult {
        OUTPUT_GENERATED, INPUT_NEEDED, ERROR
    }

    private static final Logger LOGGER = Logger.getLogger(CharsetByteProcessor.class);
    private static final char UNKNOWN_CHAR = (char) 0xFFFD;

    private final ByteBuffer inputBuffer;
    private final CharBuffer outputBuffer;

    private final CharsetDecoder decoder;
    private final CharacterProcessor callback;

    public CharsetByteProcessor(final CharacterProcessor callback, final Charset charset) {
        this.callback = callback;
        this.decoder = charset.newDecoder();

        final int maxBytes = (int) Math.ceil(charset.newEncoder().maxBytesPerChar() * 2);
        final int maxChars = (int) Math.ceil(decoder.maxCharsPerByte() * 2);

        inputBuffer = ByteBuffer.allocate(maxBytes);
        outputBuffer = CharBuffer.allocate(maxChars);

        decoder.onMalformedInput(CodingErrorAction.REPORT);
    }

    @Override
    public void processByte(final byte value) {
        try {
            inputBuffer.put(value);
        } catch(final RuntimeException e) {
            inputBuffer.clear();
            throw e;
        }
        outputBuffer.clear();

        switch(couldConvertToOutputBuffer()) {
        case OUTPUT_GENERATED:
            processCharacter();
            break;
        case INPUT_NEEDED:
            break;
        default:
            processError();
        }
    }

    private void processCharacter() {
        inputBuffer.clear();

        final char[] temp = new char[outputBuffer.limit()];
        outputBuffer.get(temp, 0, temp.length);
        for (final char c : temp) {
            callback.processChar(c);
        }
    }

    private void processError() {
        reportError();
        callback.processChar(UNKNOWN_CHAR);
        discardFirstByteAndRetry();
    }

    private void discardFirstByteAndRetry() {
        final byte[] oldContent = new byte[inputBuffer.position()];
        inputBuffer.rewind();
        inputBuffer.get(oldContent, 0, oldContent.length);
        inputBuffer.clear();

        for (int i = 1; i < oldContent.length; ++i) {
            processByte(oldContent[i]);
        }
    }

    private void reportError() {
        final StringBuilder bytes = new StringBuilder();
        final StringBuilder chars = new StringBuilder();
        for (int i = 0; i < inputBuffer.position(); ++i) {
            bytes.append(StringUtils.byteToHex(inputBuffer.get(i)));
            chars.append((char) inputBuffer.get(i));
        }
        LOGGER.error("Could not decode as " +
                decoder.charset().displayName() + ": " +
                bytes.toString() + ": " + chars.toString() +
                ", discarding first byte and retrying...");
    }

    private DecodeResult couldConvertToOutputBuffer() {
        final ByteBuffer decodeBuffer = inputBuffer.duplicate();
        decodeBuffer.flip();
        final CoderResult result = decoder.decode(decodeBuffer, outputBuffer, false);
        outputBuffer.flip();

        if (result.isUnderflow() && outputBuffer.limit() != 0) {
            return DecodeResult.OUTPUT_GENERATED;
        } else if (result.isUnderflow()) {
            return DecodeResult.INPUT_NEEDED;
        } else {
            return DecodeResult.ERROR;
        }
    }
}
