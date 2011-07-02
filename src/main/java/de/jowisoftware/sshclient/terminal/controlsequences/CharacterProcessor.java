package de.jowisoftware.sshclient.terminal.controlsequences;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.Buffer;
import de.jowisoftware.sshclient.terminal.CursorPosition;
import de.jowisoftware.sshclient.terminal.EncodingDecoder;
import de.jowisoftware.sshclient.terminal.GfxCharSetup;
import de.jowisoftware.sshclient.terminal.KeyboardFeedback;
import de.jowisoftware.sshclient.terminal.VisualFeedback;
import de.jowisoftware.sshclient.ui.GfxChar;

public class CharacterProcessor<T extends GfxChar> {
    private static final Logger LOGGER = Logger.getLogger(CharacterProcessor.class);
    private static final char ESC_CHAR = (char) 27;
    private static final char NEWLINE_CHAR = '\n';
    private static final char CARRIDGE_RETURN_CHAR = '\r';
    private static final char BELL_CHAR = 7;
    private static final char BACKSPACE_CHAR = (char) 8;

    private final LinkedList<ControlSequence<T>> availableSequences =
        new LinkedList<ControlSequence<T>>();
    private final LinkedList<ControlSequence<T>> deactivatedSequences =
        new LinkedList<ControlSequence<T>>();

    private final StringBuilder cachedChars = new StringBuilder();
    private final EncodingDecoder decoder;
    private boolean isInSequence = false;
    private final GfxCharSetup<T> setup;
    private final Buffer<T> buffer;
    private final VisualFeedback visualFeedback;
    private final KeyboardFeedback keyboardFeedback;

    public CharacterProcessor(final Buffer<T> buffer, final GfxCharSetup<T> setup,
            final Charset charset,
            final VisualFeedback visualFeedback,
            final KeyboardFeedback keyboardFeedback) {
        this.buffer = buffer;
        this.setup = setup;
        this.visualFeedback = visualFeedback;
        this.keyboardFeedback = keyboardFeedback;
        this.decoder = new EncodingDecoder(charset);
    }

    public void addControlSequence(final ControlSequence<T> seq) {
        availableSequences.add(seq);
    }

    private void resetState() {
        availableSequences.addAll(deactivatedSequences);
        deactivatedSequences.clear();
        cachedChars.delete(0, cachedChars.length());
        isInSequence = false;
    }

    public void processByte(final byte value) {
        final Character c = decoder.nextByte(value);
        if (c != null) {
            processChar(c);
        }
    }

    private void processChar(final Character c) {
        if (!isInSequence) {
            processStandardChar(c);
        } else {
            processSequenceChar(c);
        }
    }

    private void createChar(final char character) {
        if (character == NEWLINE_CHAR) {
            buffer.addNewLine();
        } else if (character == CARRIDGE_RETURN_CHAR) {
            buffer.setCursorPosition(new CursorPosition(1, buffer.getCursorPosition().getY()));
        } else if (character == BACKSPACE_CHAR) {
            buffer.setCursorPosition(buffer.getCursorPosition().offset(-1, 0));
        } else if (character == BELL_CHAR) {
            visualFeedback.bell();
        } else {
            buffer.addCharacter(setup.createChar(character));
        }
    }

    private void processStandardChar(final char character) {
        if (character == ESC_CHAR) {
            isInSequence = true;
        } else {
            createChar(character);
        }
    }

    private void processSequenceChar(final char character) {
        cachedChars.append(character);

        if(!handleFullMatches()) {
            handlePartialMatches();

            if (availableSequences.isEmpty()) {
                handleErrors();
            }
        }
    }

    private boolean handleFullMatches() {
        final Iterator<ControlSequence<T>> it = availableSequences.iterator();
        while(it.hasNext()) {
            final ControlSequence<T> seq = it.next();
            if (seq.canHandleSequence(cachedChars)) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.trace("Will handle " + cachedChars.toString() +
                            " with " + seq.getClass().getSimpleName());
                }
                seq.handleSequence(cachedChars.toString(), buffer, setup,
                        keyboardFeedback);
                resetState();
                return true;
            }
        }

        return false;
    }

    private void handlePartialMatches() {
        final Iterator<ControlSequence<T>> it = availableSequences.iterator();
        while(it.hasNext()) {
            final ControlSequence<T> seq = it.next();
            if (!seq.isPartialStart(cachedChars)) {
                it.remove();
                deactivatedSequences.addLast(seq);
            }
        }
    }

    private void handleErrors() {
        LOGGER.warn("Unable to process sequence " + cachedChars);
        resetState();

        createChar(ESC_CHAR);
        for (final char c : cachedChars.toString().toCharArray()) {
            processChar(c);
        }
    }
}
