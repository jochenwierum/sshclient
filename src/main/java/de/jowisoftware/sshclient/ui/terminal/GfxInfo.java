package de.jowisoftware.sshclient.ui.terminal;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.jowisoftware.sshclient.terminal.ColorName;

public class GfxInfo {
    private final Map<ColorName, Color> colors = new HashMap<ColorName, Color>();
    private final Map<ColorName, Color> lightColors = new HashMap<ColorName, Color>();
    private Color cursorColor;
    private Font font = new Font(Font.MONOSPACED, 0, 10);

    public GfxInfo() {
       colors.put(ColorName.BLACK, Color.BLACK);
       colors.put(ColorName.BLUE, Color.BLUE.darker());
       colors.put(ColorName.CYAN, Color.CYAN.darker());
       colors.put(ColorName.DEFAULT, Color.WHITE.darker());
       colors.put(ColorName.DEFAULT_BACKGROUND, Color.BLACK);
       colors.put(ColorName.GREEN, Color.GREEN.darker());
       colors.put(ColorName.MAGENTA, Color.MAGENTA.darker());
       colors.put(ColorName.RED, Color.RED.darker());
       colors.put(ColorName.WHITE, Color.WHITE.darker());
       colors.put(ColorName.YELLOW, Color.YELLOW.darker());

       lightColors.put(ColorName.BLACK, Color.BLACK.brighter().brighter());
       lightColors.put(ColorName.BLUE, Color.BLUE);
       lightColors.put(ColorName.CYAN, Color.CYAN);
       lightColors.put(ColorName.DEFAULT, Color.WHITE);
       lightColors.put(ColorName.DEFAULT_BACKGROUND, Color.BLACK.brighter().brighter());
       lightColors.put(ColorName.GREEN, Color.GREEN);
       lightColors.put(ColorName.MAGENTA, Color.MAGENTA);
       lightColors.put(ColorName.RED, Color.RED);
       lightColors.put(ColorName.WHITE, Color.WHITE);
       lightColors.put(ColorName.YELLOW, Color.YELLOW);

       cursorColor = Color.GREEN;
    }

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

    public Color getCursorColor() {
        return cursorColor;
    }

    public Map<ColorName, Color> getColorMap() {
        return colors;
    }

    public Map<ColorName, Color> getLightColorMap() {
        return lightColors;
    }

    public void setCursorColor(final Color color) {
        this.cursorColor = color;
    }

    public void setFont(final Font font) {
        this.font = font;
    }

    @Override
    public Object clone() {
        final GfxInfo g = new GfxInfo();
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

    public Color mapCustomColor(final int colorCode) {
        // TODO Auto-generated method stub
        return null;
    }
}
