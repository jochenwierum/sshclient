package de.jowisoftware.sshclient.ui.terminal;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.jowisoftware.sshclient.terminal.TerminalColor;

public class GfxInfo {
    private final Map<TerminalColor, Color> colors = new HashMap<TerminalColor, Color>();
    private final Map<TerminalColor, Color> lightColors = new HashMap<TerminalColor, Color>();
    private Color cursorColor;
    private Font font = new Font(Font.MONOSPACED, 0, 10);

    public GfxInfo() {
       colors.put(TerminalColor.BLACK, Color.BLACK);
       colors.put(TerminalColor.BLUE, Color.BLUE.darker());
       colors.put(TerminalColor.CYAN, Color.CYAN.darker());
       colors.put(TerminalColor.DEFAULT, Color.WHITE.darker());
       colors.put(TerminalColor.DEFAULTBG, Color.BLACK);
       colors.put(TerminalColor.GREEN, Color.GREEN.darker());
       colors.put(TerminalColor.MAGENTA, Color.MAGENTA.darker());
       colors.put(TerminalColor.RED, Color.RED.darker());
       colors.put(TerminalColor.WHITE, Color.WHITE.darker());
       colors.put(TerminalColor.YELLOW, Color.YELLOW.darker());

       lightColors.put(TerminalColor.BLACK, Color.BLACK.brighter().brighter());
       lightColors.put(TerminalColor.BLUE, Color.BLUE);
       lightColors.put(TerminalColor.CYAN, Color.CYAN);
       lightColors.put(TerminalColor.DEFAULT, Color.WHITE);
       lightColors.put(TerminalColor.DEFAULTBG, Color.BLACK.brighter().brighter());
       lightColors.put(TerminalColor.GREEN, Color.GREEN);
       lightColors.put(TerminalColor.MAGENTA, Color.MAGENTA);
       lightColors.put(TerminalColor.RED, Color.RED);
       lightColors.put(TerminalColor.WHITE, Color.WHITE);
       lightColors.put(TerminalColor.YELLOW, Color.YELLOW);

       cursorColor = Color.GREEN;
    }

    public Color mapColor(final TerminalColor color, final boolean light) {
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

    public Map<TerminalColor, Color> getColorMap() {
        return colors;
    }

    public Map<TerminalColor, Color> getLightColorMap() {
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
        for (final Entry<TerminalColor, Color> e : lightColors.entrySet()) {
            g.lightColors.put(e.getKey(), e.getValue());
        }
        for (final Entry<TerminalColor, Color> e : colors.entrySet()) {
            g.colors.put(e.getKey(), e.getValue());
        }
        g.cursorColor = cursorColor;
        g.font = font;
        return g;
    }
}
