package de.jowisoftware.sshclient.terminal.input;

import java.util.Iterator;
import java.util.Stack;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.buffer.TabulatorOrientation;
import de.jowisoftware.sshclient.terminal.input.CharacterProcessorState.State;
import de.jowisoftware.sshclient.terminal.input.controlsequences.ANSISequence;
import de.jowisoftware.sshclient.terminal.input.controlsequences.NonASCIIControlSequence;
import de.jowisoftware.sshclient.terminal.input.controlsequences.SequenceRepository;

public class SequenceSupportingCharacterProcessor implements CharacterProcessor {
    private static final Logger LOGGER = Logger.getLogger(SequenceSupportingCharacterProcessor.class);
    private static final char ESC_CHAR = (char) 27;
    private static final char NEWLINE_CHAR = '\n';
    private static final char CARRIDGE_RETURN_CHAR = '\r';
    private static final char BELL_CHAR = 7;
    private static final char BACKSPACE_CHAR = (char) 8;
    private static final char VTAB_CHAR = (char) 11;
    private static final char HTAB_CHAR = (char) 9;

    private final SequenceRepository sequenceRepository;
    private final Stack<CharacterProcessorState> states =
        new Stack<CharacterProcessorState>();

    private final SSHSession sessionInfo;
    private Character surrogateChar;

    public SequenceSupportingCharacterProcessor(final SSHSession sessionInfo,
            final SequenceRepository repository) {
        this.sessionInfo = sessionInfo;
        sequenceRepository = repository;
    }

    @Override
    public void processChar(final char character) {
        processChar(character, false);
    }

    private void processChar(final char c, final boolean handleAsNormalChar) {
        if (!handleAsNormalChar && processSpecialChar(c)) {
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

    private boolean processSpecialChar(final char character) {
        if (character == VTAB_CHAR) {
            sessionInfo.getBuffer().tabulator(TabulatorOrientation.VERTICAL);
        } else if(character == HTAB_CHAR) {
            sessionInfo.getBuffer().tabulator(TabulatorOrientation.HORIZONTAL);
        } else if (character == BACKSPACE_CHAR) {
            sessionInfo.getBuffer().processBackspace();
        } else if (character == NEWLINE_CHAR) {
            sessionInfo.getBuffer().moveCursorDown(false);
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
            sessionInfo.getVisualFeedback().fire().bell();
        } else if (surrogateChar != null) {
            final String characterString = new String(new char[]{surrogateChar,
                    character});
            sessionInfo.getBuffer().addCharacter(
                    sessionInfo.getCharSetup().createMultibyteChar(characterString));
            surrogateChar = null;
        } else if (Character.isHighSurrogate(character)) {
            surrogateChar = character;
        } else {
            sessionInfo.getBuffer().addCharacter(
                sessionInfo.getCharSetup().createChar(character));
        }
    }

    private void processFirstSequenceChar(final char c) {
        if (c == '\\') {
            resetState();
            processChar(ESC_CHAR, true);
            processChar('\\', true);
        } else if (c == '[') {
            currentState().state = State.ANSI_SEQUENCE;
        } else {
            currentState().state = State.UNKNOWN_SEQUENCE;
            processSequenceChar(c);
        }
    }

    private void processAnsiChar(final char c) {
        if (isLeagalANSISequenceContent(c)) {
            currentState().cachedChars.append(c);
        } else {
            final ANSISequence seq = sequenceRepository.getANSISequence(c);
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

    private boolean isLeagalANSISequenceContent(final char c) {
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
        final Iterator<NonASCIIControlSequence> it =
                currentState().availableSequences.iterator();

        final String cachedString = currentState().getCachedString();
        while(it.hasNext()) {
            final NonASCIIControlSequence seq = it.next();
            if (seq.canHandleSequence(cachedString)) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.trace("Will handle " +
                            cachedString +
                            " with " + seq.getClass().getSimpleName());
                }
                seq.handleSequence(cachedString, sessionInfo);
                resetState();
                return true;
            }
        }

        return false;
    }

    private void handlePartialMatches() {
        final Iterator<NonASCIIControlSequence> it =
                currentState().availableSequences.iterator();

        final String cachedString = currentState().getCachedString();
        while(it.hasNext()) {
            final NonASCIIControlSequence seq = it.next();
            if (!seq.isPartialStart(cachedString)) {
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

    private CharacterProcessorState currentState() {
        return states.peek();
    }

    private void resetState() {
        states.pop();
    }

    private void enterNewState() {
        states.push(new CharacterProcessorState(
                sequenceRepository.getNonASCIISequences()));
    }
}
