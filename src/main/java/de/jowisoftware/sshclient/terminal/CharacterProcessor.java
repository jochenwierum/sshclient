package de.jowisoftware.sshclient.terminal;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Stack;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.CharacterProcessorState.State;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.buffer.Tabstop;
import de.jowisoftware.sshclient.terminal.controlsequences.ANSISequence;
import de.jowisoftware.sshclient.terminal.controlsequences.NonASCIIControlSequence;
import de.jowisoftware.sshclient.terminal.controlsequences.SequenceRepository;

public class CharacterProcessor<T extends GfxChar> {
    private static final Logger LOGGER = Logger.getLogger(CharacterProcessor.class);
    private static final char ESC_CHAR = (char) 27;
    private static final char NEWLINE_CHAR = '\n';
    private static final char CARRIDGE_RETURN_CHAR = '\r';
    private static final char BELL_CHAR = 7;
    private static final char BACKSPACE_CHAR = (char) 8;
    private static final Character VTAB_CHAR = (char) 11;
    private static final Character HTAB_CHAR = (char) 9;

    private final SequenceRepository<T> sequenceRepository;
    private final Stack<CharacterProcessorState<T>> states =
        new Stack<CharacterProcessorState<T>>();

    private final EncodingDecoder decoder;
    private final Session<T> sessionInfo;

    public CharacterProcessor(final Session<T> sessionInfo,
            final Charset charset, final SequenceRepository<T> repository) {
        this.sessionInfo = sessionInfo;
        this.decoder = new EncodingDecoder(charset);
        sequenceRepository = repository;
    }

    public void processByte(final byte value) {
        final Character c = decoder.nextByte(value);
        if (c != null) {
            processChar(c);
        }
    }

    private void processChar(final Character c) {
        if (processSpecialChar(c)) {
            return;
        }

        if (isInNoState()) {
            processStandardChar(c);
        } else {
            switch(currentState().state) {
            case BEGIN_SEQUENCE: processFirstSequenceChar(c); break;
            case ANSI_SEQUENCE: processAnsiChar(c); break;
            case UNKNOWN_SEQUENCE: processSequenceChar(c); break;
            }
        }
    }

    private boolean processSpecialChar(final Character character) {
        if (character == VTAB_CHAR) {
            sessionInfo.getBuffer().tapstop(Tabstop.VERTICAL);
        } else if(character == HTAB_CHAR) {
            sessionInfo.getBuffer().tapstop(Tabstop.HORIZONTAL);
        } else if (character == BACKSPACE_CHAR) {
            sessionInfo.getBuffer().processBackspace();
        } else if (character == NEWLINE_CHAR) {
            sessionInfo.getBuffer().addNewLine();
        } else if (character == CARRIDGE_RETURN_CHAR) {
            sessionInfo.getBuffer().setCursorPosition(
                    new Position(1, sessionInfo.getBuffer().getCursorPosition().y));
        } else if (character == ESC_CHAR) {
            enterNewState();
        } else {
            return false;
        }
        return true;
    }

    private void processStandardChar(final char character) {
        if (character == BELL_CHAR) {
            sessionInfo.getVisualFeedback().bell();
        } else {
            sessionInfo.getBuffer().addCharacter(
                sessionInfo.getCharSetup().createChar(character));
        }
    }

    private void processFirstSequenceChar(final Character c) {
        if (c == '[') {
            currentState().state = State.ANSI_SEQUENCE;
        } else {
            currentState().state = State.UNKNOWN_SEQUENCE;
            processSequenceChar(c);
        }
    }

    private void processAnsiChar(final Character c) {
        if (isLeagalANSISequenceContent(c)) {
            currentState().cachedChars.append(c);
        } else {
            final ANSISequence<T> seq = sequenceRepository.getANSISequence(c);
            LOGGER.trace("Will handle <ESC>[" + currentState().getCachedString()
                    + c + " with " + seq.getClass().getSimpleName());

            final String sequenceText = currentState().getCachedString();
            if (sequenceText.isEmpty()) {
                seq.process(sessionInfo);
            } else {
                seq.process(sessionInfo, sequenceText.split(";"));
            }
            resetState();
        }
    }

    private boolean isLeagalANSISequenceContent(final Character c) {
        final boolean isNumber = c >= '0' && c <= '9';
        final boolean isAllowdChar = c == '?' || c == '>' || c == ';';
        return isNumber || isAllowdChar;
    }

    private void processSequenceChar(final char character) {
        currentState().cachedChars.append(character);

        if(!handleFullMatches()) {
            handlePartialMatches();

            if (currentState().availableSequences.isEmpty()) {
                handleErrors();
            }
        }
    }

    private boolean handleFullMatches() {
        final Iterator<NonASCIIControlSequence<T>> it =
                currentState().availableSequences.iterator();

        while(it.hasNext()) {
            final NonASCIIControlSequence<T> seq = it.next();
            if (seq.canHandleSequence(currentState().cachedChars)) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.trace("Will handle " +
                            currentState().getCachedString() +
                            " with " + seq.getClass().getSimpleName());
                }
                seq.handleSequence(currentState().getCachedString(), sessionInfo);
                resetState();
                return true;
            }
        }

        return false;
    }

    private void handlePartialMatches() {
        final Iterator<NonASCIIControlSequence<T>> it =
                currentState().availableSequences.iterator();

        while(it.hasNext()) {
            final NonASCIIControlSequence<T> seq = it.next();
            if (!seq.isPartialStart(currentState().cachedChars)) {
                it.remove();
            }
        }
    }

    private void handleErrors() {
        LOGGER.warn("Unable to process sequence, ignoring: " +
                currentState().cachedChars);
        resetState();
    }

    private boolean isInNoState() {
        return states.isEmpty();
    }

    private CharacterProcessorState<T> currentState() {
        return states.peek();
    }

    private void resetState() {
        states.pop();
    }

    private void enterNewState() {
        states.push(new CharacterProcessorState<T>(sequenceRepository.getNonASCIISequences()));
    }
}
