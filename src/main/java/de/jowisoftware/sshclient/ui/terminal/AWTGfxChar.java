package de.jowisoftware.sshclient.ui.terminal;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.buffer.RenderFlag;
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
        drawBackground(rect, g, flags);
        drawForeground(rect, baseLinePos, g, flags);
    }

    private void drawForeground(final Rectangle rect, final int baseLinePos,
            final Graphics g, final Set<RenderFlag> flags) {
        final Font oldFont = applyFont(g);

        applyColors(g, flags);
        drawChar(rect, baseLinePos, g, flags);

        if (oldFont != null) {
            restoreFont(g, oldFont);
        }
    }

    private void drawBackground(final Rectangle rect, final Graphics g,
            final Set<RenderFlag> flags) {
        eraseArea(rect, g, flags);

        if (flags.contains(RenderFlag.CURSOR)) {
            drawCursor(rect, g);
        }
    }

    private void drawCursor(final Rectangle rect, final Graphics g) {
        g.setColor(gfxInfo.getCursorColor());
        g.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
    }

    private void eraseArea(final Rectangle rect, final Graphics g,
            final Set<RenderFlag> flags) {
        g.setColor(getBackColor(flags));
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    private void drawChar(final Rectangle rect, final int baseLinePos,
            final Graphics g, final Set<RenderFlag> flags) {
        g.drawString(Character.toString(
                charset.getUnicodeChar(character)), rect.x,
                rect.y + baseLinePos);

        if (attributes.contains(Attribute.UNDERSCORE)) {
            g.drawLine(rect.x, rect.y, rect.x + rect.width, rect.y);
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
        return color.getColor();
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
        return color.getColor();
    }

    @Override
    public String toString() {
        return Character.toString(character);
    }
}
