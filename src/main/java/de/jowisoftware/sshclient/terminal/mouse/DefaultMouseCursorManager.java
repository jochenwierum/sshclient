package de.jowisoftware.sshclient.terminal.mouse;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.buffer.Renderer;

public class DefaultMouseCursorManager implements MouseCursorManager {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultMouseCursorManager.class);

    private static final long serialVersionUID = 3665019180187513236L;
    private final Buffer buffer;
    private final ClipboardManager clipboard;

    private Position firstClickPosition;

    private Position startPosition;
    private Position endPosition;

    private final Renderer renderer;

    public DefaultMouseCursorManager(final Buffer buffer,
            final Renderer renderer, final ClipboardManager clipboard) {
        this.buffer = buffer;
        this.clipboard = clipboard;
        this.renderer = renderer;
    }

    @Override
    public void startSelection(final Position position) {
        LOGGER.trace("Start selection: " + position);
        firstClickPosition = position;
    }

    @Override
    public void updateSelectionEnd(final Position position) {
        LOGGER.trace("End selection: " + position);
        updateSelectionFields(position);
        renderer.setSelection(startPosition, endPosition);
    }

    private void updateSelectionFields(final Position newPosition) {
        final boolean swap;

        if (firstClickPosition.y == newPosition.y) {
            swap = firstClickPosition.x > newPosition.x;
        } else {
            swap = firstClickPosition.y > newPosition.y;
        }

        if (swap) {
            startPosition = newPosition;
            endPosition = firstClickPosition;
        } else {
            startPosition = firstClickPosition;
            endPosition = newPosition;
        }
    }

    @Override
    public void copySelection() {
        LOGGER.debug("Copying range to clipboard: " + startPosition + " to "
                + endPosition);

        final String selectedText;
        if (startPosition.y == endPosition.y) {
            selectedText = getLineFromBuffer(startPosition.y,
                    startPosition.x, endPosition.x);
        } else {
            selectedText = appendSelectionToBuilder(startPosition, endPosition,
                    buffer.getSize());
        }

        clipboard.copyPlaintext(selectedText);
    }

    public String appendSelectionToBuilder(final Position pos1,
            final Position pos2, final Position size) {
        final StringBuilder builder = new StringBuilder();
        builder.append(getLineFromBuffer(pos1.y,
                pos1.x, size.x));
        builder.append("\n");

        for (int line = pos1.y + 1; line < pos2.y; ++line) {
            builder.append(getLineFromBuffer(line, 1, size.x));
            builder.append("\n");
        }
        builder.append(getLineFromBuffer(pos2.y, 1, pos2.x));
        return builder.toString();
    }

    private String getLineFromBuffer(final int line, final int from, final int to) {
        final StringBuilder builder = new StringBuilder(".");

        for (int i = from; i <= to; ++i) {
            builder.append(charAt(line, i));
        }

        return builder.toString().trim().substring(1);
    }

    private char charAt(final int y, final int x) {
        return buffer.getCharacter(y, x).getChar();
    }

}
