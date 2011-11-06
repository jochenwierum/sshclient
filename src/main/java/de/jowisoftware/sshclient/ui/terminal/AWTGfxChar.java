package de.jowisoftware.sshclient.ui.terminal;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.charsets.GfxCharset;
import de.jowisoftware.sshclient.terminal.gfx.Attribute;
import de.jowisoftware.sshclient.terminal.gfx.TerminalColor;

public class AWTGfxChar implements GfxChar {
    private final AWTGfxInfo gfxInfo;
    private final HashSet<Attribute> attributes;
    private final TerminalColor fgColor;
    private final TerminalColor bgColor;
    private final char character;
    private final GfxCharset charset;

    public AWTGfxChar(final char character,
            final GfxCharset charset, final AWTGfxInfo gfxInfo,
            final TerminalColor fgColor, final TerminalColor bgColor,
            final Set<Attribute> attributes) {
        this.character = character;
        this.charset = charset;
        this.gfxInfo = gfxInfo;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
        this.attributes = new HashSet<Attribute>(attributes);
    }

    public void drawAt(final Rectangle rect, final int baseLinePos,
            final Graphics g, final Set<RenderFlag> flags) {

        drawBackground(g, rect, flags);
        drawForeground(g, rect, flags, baseLinePos);
    }

    private void drawForeground(final Graphics g, final Rectangle rect,
            final Set<RenderFlag> flags, final int baseLinePos) {
        final Font oldFont = applyFont(g);

        applyColors(g, flags);
        drawChar(g, rect, baseLinePos);

        if (oldFont != null) {
            restoreFont(g, oldFont);
        }
    }

    private void drawBackground(final Graphics g, final Rectangle rect,
            final Set<RenderFlag> flags) {
        eraseArea(g, rect, flags);

        if (flags.contains(RenderFlag.SELECTED)) {
            drawSelection(g, rect);
        }

        if (flags.contains(RenderFlag.CURSOR)) {
            drawCursor(g, rect);
        }
    }

    private void drawSelection(final Graphics g, final Rectangle rect) {
        g.setColor(Color.RED);
        g.drawRect(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
    }

    private void drawCursor(final Graphics g, final Rectangle rect) {
        g.setColor(gfxInfo.getCursorColor());
        g.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
    }

    private void eraseArea(final Graphics g, final Rectangle rect,
            final Set<RenderFlag> flags) {
        g.setColor(getBackColor(flags));
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    private void drawChar(final Graphics g,
            final Rectangle rect, final int baseLinePos) {
        final int bottomY = rect.y + baseLinePos;
        g.drawString(Character.toString(
                charset.getUnicodeChar(character)), rect.x,
                bottomY);

        if (attributes.contains(Attribute.UNDERSCORE)) {
            g.drawLine(rect.x, bottomY, rect.x + rect.width, bottomY);
        }
    }

    private void restoreFont(final Graphics g, final Font oldFont) {
        g.setFont(oldFont);
    }

    private Font applyFont(final Graphics g) {
        final Font oldFont;
        if (attributes.contains(Attribute.BRIGHT)) {
            oldFont = g.getFont();
            g.setFont(oldFont.deriveFont(Font.BOLD));
        } else {
            oldFont = null;
        }
        return oldFont;
    }

    private void applyColors(final Graphics g, final Set<RenderFlag> flags) {
        if (!attributes.contains(Attribute.BLINK)) {
            g.setColor(getForeColor(flags));
        } else {
            if (blinkIsForeground()) {
                g.setColor(getForeColor(flags));
            } else {
                g.setColor(getBackColor(flags));
            }
        }
    }

    private boolean blinkIsForeground() {
        return (System.currentTimeMillis() / 400) % 2 == 0;
    }

    private Color getBackColor(final Set<RenderFlag> flags) {
        final TerminalColor color;
        if (invertBackground(flags)) {
            color = fgColor;
        } else {
            color = bgColor;
        }

        if (flags.contains(RenderFlag.INVERTED)) {
            return (Color) color.invert().getColor();
        } else {
            return (Color) color.getColor();
        }
    }

    private boolean invertBackground(final Set<RenderFlag> flags) {
        final boolean inversed = attributes.contains(Attribute.INVERSE);
        final boolean selected = flags.contains(RenderFlag.SELECTED);

        return inversed != selected;
    }

    private Color getForeColor(final Set<RenderFlag> flags) {
        final TerminalColor color;
        if (invertBackground(flags)) {
            color = bgColor;
        } else {
            color = fgColor;
        }

        if (flags.contains(RenderFlag.INVERTED)) {
            return (Color) color.invert().getColor();
        } else {
            return (Color) color.getColor();
        }
    }

    @Override
    public String toString() {
        return Character.toString(character);
    }

    @Override
    public char getChar() {
        return character;
    }
}
