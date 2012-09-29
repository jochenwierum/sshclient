package de.jowisoftware.sshclient.ui.terminal;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.terminal.gfx.CursorStyle;
import de.jowisoftware.sshclient.terminal.gfx.GfxInfo;

public final class AWTGfxInfo implements GfxInfo<Color>, Cloneable {
    private Map<ColorName, Color> colors = new HashMap<ColorName, Color>();
    private Map<ColorName, Color> lightColors = new HashMap<ColorName, Color>();
    private Color cursorColor;

    private final int screenRes = Toolkit.getDefaultToolkit().getScreenResolution();
    private Font font;
    private Font boldFont;
    private String fontName;
    private int fontSize;
    private int antiAliasingMode;
    private CursorStyle cursorStyle;
    private boolean cursorBlinks;

    private String boundaryChars = ":@-./_~?&=%+#";

    public AWTGfxInfo() {
       setFont(Font.MONOSPACED, 10);

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
       cursorBlinks = true;
       cursorStyle = CursorStyle.Block;
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

    public void setFont(final String fontName, final int fontSize) {
        final int realFontSize = (int)Math.round(fontSize * screenRes / 72.0);
        this.fontSize = fontSize;
        this.fontName = fontName;
        this.font = new Font(fontName, Font.PLAIN, realFontSize);
        this.boldFont = font.deriveFont(Font.BOLD);
    }

    public String getFontName() {
        return fontName;
    }

    public int getFontSize() {
        return fontSize;
    }

    @Override
    public Object clone() {
        AWTGfxInfo clone;
        try {
            clone = (AWTGfxInfo) super.clone();
        } catch (final CloneNotSupportedException e1) {
            throw new RuntimeException(e1);
        }

        clone.lightColors = new HashMap<ColorName, Color>();
        copyColorMap(lightColors, clone.lightColors);

        clone.colors = new HashMap<ColorName, Color>();
        copyColorMap(colors, clone.colors);

        return clone;
    }

    private void copyColorMap(
            final Map<ColorName, Color> source,
            final Map<ColorName, Color> destination) {
        for (final Entry<ColorName, Color> e : source.entrySet()) {
            destination.put(e.getKey(), e.getValue());
        }
    }

    public void setAntiAliasingMode(final int antiAliasingMode) {
        this.antiAliasingMode = antiAliasingMode;
    }

    public int getAntiAliasingMode() {
        return antiAliasingMode;
    }

    @Override
    public String getBoundaryChars() {
        return boundaryChars;
    }

    @Override
    public void setBoundaryChars(final String boundaryChars) {
        this.boundaryChars = boundaryChars;
    }

    @Override
    public CursorStyle getCursorStyle() {
        return cursorStyle;
    }

    public void setCursorStyle(final CursorStyle cursorStyle) {
        this.cursorStyle = cursorStyle;
    }

    @Override
    public boolean cursorBlinks() {
        return cursorBlinks;
    }

    public void setCursorBlinks(final boolean cursorBlinks) {
        this.cursorBlinks = cursorBlinks;
    }
}
