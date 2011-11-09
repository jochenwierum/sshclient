package de.jowisoftware.sshclient.ui.terminal;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.terminal.gfx.GfxInfo;

public final class AWTGfxInfo implements GfxInfo<Color>, Cloneable {
    private final Map<ColorName, Color> colors = new HashMap<ColorName, Color>();
    private final Map<ColorName, Color> lightColors = new HashMap<ColorName, Color>();
    private Color cursorColor;
    private Font font = new Font(Font.MONOSPACED, 0, 10);
    private Font boldFont = new Font(Font.MONOSPACED, Font.BOLD, 10);

    public AWTGfxInfo() {
       colors.put(ColorName.BLACK, color(0, 0, 0));
       colors.put(ColorName.BLUE, color(0, 0, 238));
       colors.put(ColorName.CYAN, color(0, 205, 205));
       colors.put(ColorName.DEFAULT, color(229, 229, 229));
       colors.put(ColorName.DEFAULT_BACKGROUND, color(0, 0, 0));
       colors.put(ColorName.GREEN, color(0, 205, 0));
       colors.put(ColorName.MAGENTA, color(205, 0, 205));
       colors.put(ColorName.RED, color(205, 0, 0));
       colors.put(ColorName.WHITE, color(229, 229, 229));
       colors.put(ColorName.YELLOW, color(205, 205, 0));

       lightColors.put(ColorName.BLACK, color(127, 127, 127));
       lightColors.put(ColorName.BLUE, color(92, 92, 255));
       lightColors.put(ColorName.CYAN, color(0, 255, 255));
       lightColors.put(ColorName.DEFAULT, color(229, 229, 229));
       lightColors.put(ColorName.DEFAULT_BACKGROUND, color(0, 0, 0));
       lightColors.put(ColorName.GREEN, color(0, 255, 0));
       lightColors.put(ColorName.MAGENTA, color(255, 0, 255));
       lightColors.put(ColorName.RED, color(255, 0, 0));
       lightColors.put(ColorName.WHITE, color(255, 255, 255));
       lightColors.put(ColorName.YELLOW, color(255, 255, 0));

       cursorColor = Color.GREEN;
    }

    private static Color color(final int r, final int g, final int b) {
        return new Color(r, g, b);
    }

    @Override
    public Color mapColor(final ColorName color, final boolean light) {
        if (!light) {
            return colors.get(color);
        } else {
            return lightColors.get(color);
        }
    }

    public Font getFont() {
        return font;
    }

    public Font getBoldFont() {
        return boldFont;
    }

    @Override
    public Color getCursorColor() {
        return cursorColor;
    }

    @Override
    public Map<ColorName, Color> getColorMap() {
        return colors;
    }

    @Override
    public Map<ColorName, Color> getLightColorMap() {
        return lightColors;
    }

    @Override
    public void setCursorColor(final Color color) {
        this.cursorColor = color;
    }

    public void setFont(final Font font) {
        this.font = font;
        if (font != null) {
            this.boldFont = font.deriveFont(Font.BOLD);
        } else {
            this.boldFont = null;
        }
    }

    @Override
    public Object clone() {
        final AWTGfxInfo g = new AWTGfxInfo();
        for (final Entry<ColorName, Color> e : lightColors.entrySet()) {
            g.lightColors.put(e.getKey(), e.getValue());
        }
        for (final Entry<ColorName, Color> e : colors.entrySet()) {
            g.colors.put(e.getKey(), e.getValue());
        }
        g.cursorColor = cursorColor;
        g.font = font;
        return g;
    }
}
