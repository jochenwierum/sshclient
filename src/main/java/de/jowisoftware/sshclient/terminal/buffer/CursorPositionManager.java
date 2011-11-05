package de.jowisoftware.sshclient.terminal.buffer;

public interface CursorPositionManager {
    public static final int NO_MARGIN_DEFINED = -1;

    void newSize(int width, int height);

    Position currentPositionInScreen();
    Position currentPositionInMargin();
    boolean isAt(int col, int row);

    void setPositionSafelyInScreen(Position newPosition);
    void setPositionSafelyInMargin(Position position);

    void restore();
    void save();

    void resetWouldWrap();
    boolean wouldWrap();

    void moveToNextPosition();

    void setMargins(int rollRangeBegin, int rollRangeEnd);
    int getBottomMargin();
    boolean isMarginDefined();

    void moveDownAndRoll();
    void moveUpAndRoll();
}
