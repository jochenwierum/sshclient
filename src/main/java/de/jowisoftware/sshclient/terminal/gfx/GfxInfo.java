package de.jowisoftware.sshclient.terminal.gfx;

import java.util.Map;

public interface GfxInfo<C> {
    C mapColor(final ColorName color, final boolean light);
    C getCursorColor();
    Map<ColorName, C> getColorMap();
    Map<ColorName, C> getLightColorMap();
    void setCursorColor(final C color);

    CursorStyle getCursorStyle();
    boolean cursorBlinks();

    String getBoundaryChars();
    void setBoundaryChars(String boundaryChars);

    void setAntiAliasingMode(int selectedIndex);
    int getAntiAliasingMode();

    void setFontName(String fontName);
    String getFontName();

    void setFontSize(int integer);
    int getFontSize();

    void setCursorStyle(CursorStyle cursorStyle);
    void setCursorBlinks(boolean b);

}
