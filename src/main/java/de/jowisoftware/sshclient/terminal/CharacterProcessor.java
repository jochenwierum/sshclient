package de.jowisoftware.sshclient.terminal;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.controlsequences.NonASCIIControlSequence;
import de.jowisoftware.sshclient.ui.GfxChar;
import de.jowisoftware.sshclient.util.SequenceUtils;

public class CharacterProcessor<T extends GfxChar> {
    private static enum State {
        NO_SEQUENCE, BEGIN_SEQUENCE, ANSI_SEQUENCE, UNKNOWN_SEQUENCE;
    }

    private static final Logger LOGGER = Logger.getLogger(CharacterProcessor.class);
    private static final char ESC_CHAR = (char) 27;
    private static final char NEWLINE_CHAR = '\n';
    private static final char CARRIDGE_RETURN_CHAR = '\r';
    private static final char BELL_CHAR = 7;
    private static final char BACKSPACE_CHAR = (char) 8;

    private final LinkedList<NonASCIIControlSequence<T>> availableSequences =
        new LinkedList<NonASCIIControlSequence<T>>();
    private final LinkedList<NonASCIIControlSequence<T>> deactivatedSequences =
        new LinkedList<NonASCIIControlSequence<T>>();

    private final StringBuilder cachedChars = new StringBuilder();
    private final EncodingDecoder decoder;
    private final SessionInfo<T> sessionInfo;
    private State state = State.NO_SEQUENCE;

    public CharacterProcessor(final SessionInfo<T> sessionInfo,
            final Charset charset) {
        this.sessionInfo = sessionInfo;
        this.decoder = new EncodingDecoder(charset);
    }

    public void addControlSequence(final NonASCIIControlSequence<T> seq) {
        availableSequences.add(seq);
    }

    private void resetState() {
        availableSequences.addAll(deactivatedSequences);
        deactivatedSequences.clear();
        cachedChars.delete(0, cachedChars.length());
        state = State.NO_SEQUENCE;
    }

    public void processByte(final byte value) {
        final Character c = decoder.nextByte(value);
        if (c != null) {
            processChar(c);
        }
    }

    private void processChar(final Character c) {
        switch(state) {
        case NO_SEQUENCE: processStandardChar(c); break;
        case BEGIN_SEQUENCE: processFirstSequenceChar(c); break;
        case ANSI_SEQUENCE: processAnsiChar(c); break;
        case UNKNOWN_SEQUENCE: processSequenceChar(c); break;
        }
    }

    private void processStandardChar(final char character) {
        if (character == ESC_CHAR) {
            state = State.BEGIN_SEQUENCE;
        } else {
            createChar(character);
        }
    }

    private void processFirstSequenceChar(final Character c) {
        if (c == '[') {
            state = State.ANSI_SEQUENCE;
        } else {
            state = State.UNKNOWN_SEQUENCE;
            processSequenceChar(c);
        }
    }

    private void processAnsiChar(final Character c) {
        if (isLeagalANSISequenceContent(c)) {
            cachedChars.append(c);
        } else {
            if (cachedChars.length() == 0) {
                SequenceUtils.executeAnsiSequence(c, sessionInfo);
            } else {
                SequenceUtils.executeAnsiSequence(c, sessionInfo,
                        cachedChars.toString().split(";"));
            }
            resetState();
        }
    }

    private boolean isLeagalANSISequenceContent(final Character c) {
        return c == '?' || c == '>' || (c >= '0' && c <= '9') || c == ';';
    }

    private void createChar(final char character) {
        if (character == NEWLINE_CHAR) {
            sessionInfo.getBuffer().addNewLine();
        } else if (character == CARRIDGE_RETURN_CHAR) {
            sessionInfo.getBuffer().setCursorPosition(
                    new CursorPosition(1, sessionInfo.getBuffer().getCursorPosition().getY()));
        } else if (character == BACKSPACE_CHAR) {
            sessionInfo.getBuffer().setCursorPosition(
                    sessionInfo.getBuffer().getCursorPosition().offset(-1, 0));
        } else if (character == BELL_CHAR) {
            sessionInfo.getVisualFeedback().bell();
        } else {
            sessionInfo.getBuffer().addCharacter(
                    sessionInfo.getCharSetup().createChar(character));
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
        final Iterator<NonASCIIControlSequence<T>> it = availableSequences.iterator();
        while(it.hasNext()) {
            final NonASCIIControlSequence<T> seq = it.next();
            if (seq.canHandleSequence(cachedChars)) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.trace("Will handle " + cachedChars.toString() +
                            " with " + seq.getClass().getSimpleName());
                }
                seq.handleSequence(cachedChars.toString(), sessionInfo);
                resetState();
                return true;
            }
        }

        return false;
    }

    private void handlePartialMatches() {
        final Iterator<NonASCIIControlSequence<T>> it = availableSequences.iterator();
        while(it.hasNext()) {
            final NonASCIIControlSequence<T> seq = it.next();
            if (!seq.isPartialStart(cachedChars)) {
                it.remove();
                deactivatedSequences.addLast(seq);
            }
        }
    }

    private void handleErrors() {
        LOGGER.warn("Unable to process sequence, ignoring: " + cachedChars);
        resetState();
    }
}
