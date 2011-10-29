package de.jowisoftware.sshclient.terminal;

import java.util.Map;


public interface GfxInfo<C> {
    C mapColor(final ColorName color, final boolean light);
    C getCursorColor();
    Map<ColorName, C> getColorMap();
    Map<ColorName, C> getLightColorMap();
    void setCursorColor(final C color);
    C mapCustomColor(final int colorCode);
}