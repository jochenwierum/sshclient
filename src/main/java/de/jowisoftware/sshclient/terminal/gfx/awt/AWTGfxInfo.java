package de.jowisoftware.sshclient.terminal.gfx.awt;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;

import com.sun.javafx.binding.StringFormatter;
import de.jowisoftware.sshclient.persistence.annotations.Persist;
import de.jowisoftware.sshclient.persistence.annotations.TraversalType;
import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.terminal.gfx.CursorStyle;
import de.jowisoftware.sshclient.terminal.gfx.GfxInfo;

public final class AWTGfxInfo implements GfxInfo<Color> {
    @Persist(traversalType = TraversalType.MAP, targetClass = Color.class, targetClass2 = ColorName.class)
    private final Map<ColorName, Color> colors;
    @Persist(traversalType = TraversalType.MAP, targetClass = Color.class, targetClass2 = ColorName.class)
    private final Map<ColorName, Color> lightColors;

    @Persist("cursorColor") private Color cursorColor = Color.GREEN;

    private final int screenRes = Toolkit.getDefaultToolkit().getScreenResolution();

    private Font font;
    private Font boldFont;
    @Persist("font") private String fontName = Font.MONOSPACED;
    @Persist("fontSize") private int fontSize = 10;
    @Persist("antiAliasingMode") private int antiAliasingMode;
    @Persist("cursorStyle") private CursorStyle cursorStyle = CursorStyle.Block;
    @Persist("cursorStyle/@blink") private boolean cursorBlinks = true;

    @Persist("boundaryChars") private String boundaryChars = ":@-./_~?&=%+#";

    public AWTGfxInfo() {
       colors = createDefaultColors();
       lightColors = createDefaultLightColors();
    }

    public AWTGfxInfo(final AWTGfxInfo copy) {
        colors = new HashMap<>(copy.colors);
        lightColors = new HashMap<>(copy.lightColors);

        cursorColor = copy.cursorColor;
        font = copy.font;
        boldFont = copy.boldFont;
        fontName = copy.fontName;
        fontSize = copy.fontSize;
        antiAliasingMode = copy.antiAliasingMode;
        cursorStyle = copy.cursorStyle;
        cursorBlinks = copy.cursorBlinks;
        boundaryChars = copy.boundaryChars;
    }

    private Map<ColorName, Color> createDefaultLightColors() {
        final Map<ColorName, Color> result = new HashMap<>();
        result.put(ColorName.BLACK, color(127, 127, 127));
        result.put(ColorName.BLUE, color(92, 92, 255));
        result.put(ColorName.CYAN, color(0, 255, 255));
        result.put(ColorName.DEFAULT, color(229, 229, 229));
        result.put(ColorName.DEFAULT_BACKGROUND, color(0, 0, 0));
        result.put(ColorName.GREEN, color(0, 255, 0));
        result.put(ColorName.MAGENTA, color(255, 0, 255));
        result.put(ColorName.RED, color(255, 0, 0));
        result.put(ColorName.WHITE, color(255, 255, 255));
        result.put(ColorName.YELLOW, color(255, 255, 0));
        return result;
    }

    private Map<ColorName, Color> createDefaultColors() {
        final Map<ColorName, Color> result = new HashMap<>();
        result.put(ColorName.BLACK, color(0, 0, 0));
        result.put(ColorName.BLUE, color(0, 0, 238));
        result.put(ColorName.CYAN, color(0, 205, 205));
        result.put(ColorName.DEFAULT, color(229, 229, 229));
        result.put(ColorName.DEFAULT_BACKGROUND, color(0, 0, 0));
        result.put(ColorName.GREEN, color(0, 205, 0));
        result.put(ColorName.MAGENTA, color(205, 0, 205));
        result.put(ColorName.RED, color(205, 0, 0));
        result.put(ColorName.WHITE, color(229, 229, 229));
        result.put(ColorName.YELLOW, color(205, 205, 0));
        return result;
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
        assureFontIsInitialized();
        return font;
    }

    public Font getBoldFont() {
        assureFontIsInitialized();
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

    private void assureFontIsInitialized() {
        final int realFontSize = (int)Math.round(fontSize * screenRes / 72.0);
        this.font = new Font(fontName, Font.PLAIN, realFontSize);
        this.boldFont = font.deriveFont(Font.BOLD);
    }

    public void setFontName(final String name) {
        font = null;
        boldFont = null;
        this.fontName = name;
    }

    public void setFontSize(final int size) {
        font = null;
        boldFont = null;
        this.fontSize = size;
    }

    public String getFontName() {
        return fontName;
    }

    public int getFontSize() {
        return fontSize;
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
