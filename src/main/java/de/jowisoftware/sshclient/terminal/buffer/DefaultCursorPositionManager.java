package de.jowisoftware.sshclient.terminal.buffer;

import org.apache.log4j.Logger;

public class DefaultCursorPositionManager implements CursorPositionManager {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultCursorPositionManager.class);

    private Position position = new Position(1, 1);
    private Position savedPosition;
    private Position rightBottomCorner;

    private int topMargin = NO_MARGIN_DEFINED;
    private int bottomMargin = NO_MARGIN_DEFINED;

    private boolean wouldWrap;

    private final CursorPositionManagerFeedback feedback;

    public DefaultCursorPositionManager(final CursorPositionManagerFeedback feedback,
            final int width, final int height) {
        this.feedback = feedback;
        newSize(width, height);
    }

    @Override
    public void newSize(final int width, final int height) {
        rightBottomCorner = new Position(width, height);
        position = position.moveInRange(rightBottomCorner.toRange());
    }

    @Override
    public Position currentPositionInScreen() {
        return position;
    }

    @Override
    public void restore() {
        setPositionSafelyInScreen(savedPosition);
    }

    @Override
    public void save() {
        savedPosition = position;
    }

    @Override
    public void setPositionSafelyInScreen(final Position newPosition) {
        if (newPosition.y > rightBottomCorner.y) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("invalid terminal position, shifting lines: " +
                        newPosition.x + "/" + newPosition.y);
            }
            final int offset = (-newPosition.y - rightBottomCorner.y) % rightBottomCorner.y;
            feedback.lineShiftingNeeded(offset, 0, rightBottomCorner.y);
        }

        position = newPosition.moveInRange(rightBottomCorner.toRange());
        wouldWrap = false;
    }

    @Override
    public boolean wouldWrap() {
        return wouldWrap;
    }

    @Override
    public void resetWouldWrap() {
        wouldWrap = false;
    }

    @Override
    public void moveToNextPosition(final int characterWidth) {
        final Position newPosition = position.offset(characterWidth, 0);
        wouldWrap = (newPosition.x >= rightBottomCorner.x + 1);
        position = newPosition.moveInRange(rightBottomCorner.toRange());
    }

    @Override
    public boolean isMarginDefined() {
        return topMargin != NO_MARGIN_DEFINED && bottomMargin != NO_MARGIN_DEFINED;
    }

    @Override
    public void setPositionSafelyInMargin(final Position position) {
        Position screenPosition = position.offset(0, topMargin - 1);

        if (screenPosition.y > bottomMargin) {
            feedback.lineShiftingNeeded(bottomMargin - screenPosition.y,
                    topMargin, bottomMargin);
            screenPosition = screenPosition.withY(bottomMargin);
        } else if (screenPosition.y < topMargin) {
            feedback.lineShiftingNeeded(topMargin - screenPosition.y,
                    topMargin, bottomMargin);
            screenPosition = screenPosition.withY(topMargin);
        }


        setPositionSafelyInScreen(screenPosition);
    }

    @Override
    public Position currentPositionInMargin() {
        return position.offset(0, -topMargin + 1);
    }

    @Override
    public void setMargins(final int topMargin, final int bottomMargin) {
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
    }

    @Override
    public int getBottomMargin() {
        return bottomMargin;
    }

    @Override
    public void moveDownAndRoll() {
        int y = position.y;
        if (!isMarginDefined()) {
            setPositionSafelyInScreen(position.withY(y + 1));
        } else {
            if (y == bottomMargin) {
                feedback.lineShiftingNeeded(-1, topMargin - 1, bottomMargin);
            } else {
                ++y;
            }
            setPositionSafelyInScreen(position.withY(y));
        }
    }

    @Override
    public void moveUpAndRoll() {
        if (!isMarginDefined()) {
            if (position.y == 1) {
                feedback.lineShiftingNeeded(1, 0, rightBottomCorner.y);
            } else {
                position = position.offset(0, -1);
            }
        } else {
            if (position.y == topMargin) {
                feedback.lineShiftingNeeded(1, topMargin - 1, bottomMargin);
            } else {
                position = position.offset(0, -1);
            }
        }
    }
}
