package de.jowisoftware.sshclient.terminal.buffer;

public class WordBoundaryLocator implements BoundaryLocator {
    private final Buffer buffer;
    private String selectionChars = "";

    public WordBoundaryLocator(final Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public Position findStartOfWord(final Position position) {
        Position testedPosition = recoverPositionWhenBeyondMaximum(position);
        Position result = testedPosition;

        while(testedPosition.x > 0 && isWordChar(testedPosition)) {
            result = testedPosition;
            testedPosition = testedPosition.offset(-1, 0);
        }

        return result;
    }

    private Position recoverPositionWhenBeyondMaximum(Position testedPosition) {
        final int maxX = buffer.getSize().x;
        if (testedPosition.x > maxX) {
            testedPosition = testedPosition.withX(maxX);
        }
        return testedPosition;
    }

    private boolean isWordChar(final Position position) {
        final String charString = buffer.getCharacter(position.y, position.x).getCharAsString();
        if (charString == null) {
            return false;
        }

        final int codePoint = Character.codePointAt(charString, 0);
        return Character.isLetterOrDigit(codePoint) || selectionChars.contains(charString);
    }

    @Override
    public Position findEndOfWord(final Position position) {
        Position testedPosition = recoverPositionWhenBeyondMaximum(position);
        Position result = testedPosition;
        final int maxX = buffer.getSize().x;

        while(testedPosition.x <= maxX && isWordChar(testedPosition)) {
            result = testedPosition;
            testedPosition = testedPosition.offset(1, 0);
        }

        return result;
    }

    @Override
    public void setSelectionChars(final String string) {
        this.selectionChars = string;
    }
}
