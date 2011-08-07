package de.jowisoftware.sshclient.ui.terminal;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import de.jowisoftware.sshclient.terminal.Attribute;
import de.jowisoftware.sshclient.terminal.TerminalColor;
import de.jowisoftware.sshclient.ui.terminal.charsets.USASCIICharset;

public class GfxInfo {
    private final Map<TerminalColor, Color> colors = new HashMap<TerminalColor, Color>();
    private final Map<TerminalColor, Color> brightColors = new HashMap<TerminalColor, Color>();
    private final GfxAwtChar emptyChar = new GfxAwtChar(' ', new USASCIICharset(),
            this, TerminalColor.DEFAULTBG, TerminalColor.DEFAULTBG,
            new HashSet<Attribute>());
    private Color cursorColor;
    private Font font = new Font(Font.MONOSPACED, 0, 10);

    public GfxInfo() {
       colors.put(TerminalColor.BLACK, Color.BLACK);
       colors.put(TerminalColor.BLUE, Color.BLUE.darker());
       colors.put(TerminalColor.CYAN, Color.CYAN.darker());
       colors.put(TerminalColor.DEFAULT, Color.LIGHT_GRAY.darker());
       colors.put(TerminalColor.DEFAULTBG, Color.BLACK);
       colors.put(TerminalColor.GREEN, Color.GREEN.darker());
       colors.put(TerminalColor.MAGENTA, Color.MAGENTA.darker());
       colors.put(TerminalColor.RED, Color.RED.darker());
       colors.put(TerminalColor.WHITE, Color.WHITE);
       colors.put(TerminalColor.YELLOW, Color.YELLOW.darker());

       brightColors.put(TerminalColor.BLACK, Color.BLACK);
       brightColors.put(TerminalColor.BLUE, Color.BLUE);
       brightColors.put(TerminalColor.CYAN, Color.CYAN);
       brightColors.put(TerminalColor.DEFAULT, Color.WHITE);
       brightColors.put(TerminalColor.DEFAULTBG, Color.BLACK);
       brightColors.put(TerminalColor.GREEN, Color.GREEN);
       brightColors.put(TerminalColor.MAGENTA, Color.MAGENTA);
       brightColors.put(TerminalColor.RED, Color.RED);
       brightColors.put(TerminalColor.WHITE, Color.WHITE);
       brightColors.put(TerminalColor.YELLOW, Color.YELLOW);

       cursorColor = Color.GREEN;
    }

    public Color mapColor(final TerminalColor color, final boolean light) {
        if (!light) {
            return colors.get(color);
        } else {
            return brightColors.get(color);
        }
    }

    public Font getFont() {
        return font;
    }

    public GfxAwtChar getEmptyChar() {
        return emptyChar;
    }

    public Color getCursorColor() {
        return cursorColor;
    }

    public Map<TerminalColor, Color> getColorMap() {
        return colors;
    }

    public Map<TerminalColor, Color> getLightColorMap() {
        return brightColors;
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
        for (final Entry<TerminalColor, Color> e : brightColors.entrySet()) {
            g.brightColors.put(e.getKey(), e.getValue());
        }
        for (final Entry<TerminalColor, Color> e : colors.entrySet()) {
            g.colors.put(e.getKey(), e.getValue());
        }
        g.cursorColor = cursorColor;
        g.font = font;
        return g;
    }
}
