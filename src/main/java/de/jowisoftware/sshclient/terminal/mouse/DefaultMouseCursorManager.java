package de.jowisoftware.sshclient.terminal.mouse;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.buffer.BoundaryLocator;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.buffer.Renderer;
import de.jowisoftware.sshclient.terminal.buffer.Snapshot;

public class DefaultMouseCursorManager implements MouseCursorManager {
    private enum SelectionMode {
        SELECTION_MODE, WORDWISE, LINEWIESE
    }

    private static final Logger LOGGER = Logger
            .getLogger(DefaultMouseCursorManager.class);

    private final Buffer buffer;
    private final ClipboardManager clipboard;
    private final Renderer renderer;
    private final BoundaryLocator boundaryLocator;

    private Position firstClickPosition;
    private Position lastSelectionEndPosition;
    private SelectionMode selectionMode = SelectionMode.SELECTION_MODE;

    private Position startPosition;
    private Position endPosition;

    private int renderOffset;

    public DefaultMouseCursorManager(final Buffer buffer,
            final Renderer renderer, final ClipboardManager clipboard,
            final BoundaryLocator wordBoundaryLocator) {
        this.buffer = buffer;
        this.clipboard = clipboard;
        this.renderer = renderer;
        this.boundaryLocator = wordBoundaryLocator;
    }

    @Override
    public void startSelection(final Position position, final int clicks) {
        LOGGER.trace("Start selection: " + position);
        selectionMode = SelectionMode.values()[Math.min(clicks - 1, 2)];
        firstClickPosition = position;
        lastSelectionEndPosition = null;
    }

    @Override
    public void updateSelectionEnd(final Position position) {
        if (firstClickPosition == null) {
            return;
        }

        if (!position.equals(lastSelectionEndPosition)) {
            LOGGER.trace("End selection: " + position);
            if (position.equals(firstClickPosition) && selectionMode == SelectionMode.SELECTION_MODE) {
                renderer.clearSelection();
                startPosition = null;
                endPosition = null;
            } else {
                updateSelectionFields(position);
                renderer.setSelection(startPosition, endPosition.offset(-1, 0));
            }
            lastSelectionEndPosition = position;
        }
    }

    private void updateSelectionFields(final Position newPosition) {
        final boolean swap = firstClickPosition.isAfter(newPosition);

        switch(selectionMode) {
        case SELECTION_MODE:
            updateCharwiseSelectionFields(firstClickPosition, newPosition, swap);
            break;
        case WORDWISE:
            updateWordwiseSelectionFields(firstClickPosition, newPosition, swap);
            break;
        case LINEWIESE:
            updateLinewiseSelectionFields(firstClickPosition, newPosition, swap);
            break;
        }
    }

    private void updateWordwiseSelectionFields(final Position pos1,
            final Position pos2, final boolean swap) {
        if (!swap) {
            startPosition = boundaryLocator.findStartOfWord(pos1);
            endPosition = boundaryLocator.findEndOfWord(pos2).offset(1, 0);
        } else {
            startPosition = boundaryLocator.findStartOfWord(pos2);
            endPosition = boundaryLocator.findEndOfWord(pos1).offset(1, 0);
        }
    }

    private void updateLinewiseSelectionFields(final Position pos1,
            final Position pos2, final boolean swap) {
        if (!swap) {
            startPosition = pos1.withX(1);
            endPosition = pos2.withX(buffer.getSize().x + 1);
        } else {
            startPosition = pos2.withX(1);
            endPosition = pos1.withX(buffer.getSize().x + 1);
        }
    }

    private void updateCharwiseSelectionFields(final Position pos1,
            final Position pos2, final boolean swap) {
        if (!swap) {
            startPosition = pos1;
            endPosition = pos2;
        } else {
            startPosition = pos2;
            endPosition = pos1;
        }
    }

    @Override
    public void copySelection() {
        if (startPosition == null || endPosition == null) {
            return;
        }

        final Snapshot snapshot = buffer.createSnapshot().createSimpleSnapshot(renderOffset);

        LOGGER.debug("Copying range to clipboard: " + startPosition + " to "
                + endPosition);

        final String selectedText;
        if (startPosition.y == endPosition.y) {
            selectedText = getLineFromSnapshot(snapshot, startPosition.y,
                    startPosition.x, endPosition.x - 1);
        } else {
            selectedText = appendSelectionToBuilder(snapshot,
                    startPosition, endPosition, buffer.getSize());
        }

        clipboard.copyPlaintext(selectedText);
    }

    private String appendSelectionToBuilder(final Snapshot snapshot, final Position pos1,
            final Position pos2, final Position size) {
        final StringBuilder builder = new StringBuilder();
        builder.append(getLineFromSnapshot(snapshot, pos1.y, pos1.x, size.x));
        builder.append("\n");

        for (int line = pos1.y + 1; line < pos2.y; ++line) {
            builder.append(getLineFromSnapshot(snapshot, line, 1, size.x));
            builder.append("\n");
        }
        builder.append(getLineFromSnapshot(snapshot, pos2.y, 1, pos2.x - 1));
        return builder.toString();
    }

    private String getLineFromSnapshot(final Snapshot snapshot, final int line,
            final int from, final int to) {
        final StringBuilder builder = new StringBuilder(".");

        for (int i = from; i <= to; i += charWidth(snapshot, line, i)) {
            builder.append(charAt(snapshot, line, i));
        }

        return builder.toString().trim().substring(1);
    }

    private int charWidth(final Snapshot snapshot, final int y, final int x) {
        if (snapshot.content[y - 1].length < x) {
            return 1;
        } else {
            return snapshot.content[y - 1][x - 1].getCharCount();
        }
    }

    private String charAt(final Snapshot snapshot, final int y, final int x) {
        if (snapshot.content[y - 1].length < x) {
            return " ";
        } else {
            return snapshot.content[y - 1][x - 1].getCharAsString();
        }
    }

    @Override
    public void setRenderOffset(final int offset) {
        this.renderOffset = offset;
    }
}
