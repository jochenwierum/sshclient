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
            final Graphics g, final Set<RenderFlag> flags,
            final boolean inverted) {

        drawBackground(rect, g, flags, inverted);
        drawForeground(rect, baseLinePos, g, flags, inverted);
    }

    private void drawForeground(final Rectangle rect, final int baseLinePos,
            final Graphics g, final Set<RenderFlag> flags, final boolean inverted) {
        final Font oldFont = applyFont(g);

        applyColors(g, flags, inverted);
        drawChar(rect, baseLinePos, g, flags);

        if (oldFont != null) {
            restoreFont(g, oldFont);
        }
    }

    private void drawBackground(final Rectangle rect, final Graphics g,
            final Set<RenderFlag> flags, final boolean inverted) {
        eraseArea(rect, g, flags, inverted);

        if (flags.contains(RenderFlag.CURSOR)) {
            drawCursor(rect, g);
        }
    }

    private void drawCursor(final Rectangle rect, final Graphics g) {
        g.setColor(gfxInfo.getCursorColor());
        g.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
    }

    private void eraseArea(final Rectangle rect, final Graphics g,
            final Set<RenderFlag> flags, final boolean inverted) {
        g.setColor(getBackColor(flags, inverted));
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    private void drawChar(final Rectangle rect, final int baseLinePos,
            final Graphics g, final Set<RenderFlag> flags) {
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

    private void applyColors(final Graphics g, final Set<RenderFlag> flags,
            final boolean inverted) {
        if (!attributes.contains(Attribute.BLINK)) {
            g.setColor(getForeColor(flags, inverted));
        } else {
            if (blinkIsForeground()) {
                g.setColor(getForeColor(flags, inverted));
            } else {
                g.setColor(getBackColor(flags, inverted));
            }
        }
    }

    private boolean blinkIsForeground() {
        return (System.currentTimeMillis() / 400) % 2 == 0;
    }

    private Color getBackColor(final Set<RenderFlag> flags,
            final boolean inverted) {
        final TerminalColor color;
        if (invertBackground(flags)) {
            color = fgColor;
        } else {
            color = bgColor;
        }

        if (inverted) {
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

    private Color getForeColor(final Set<RenderFlag> flags, final boolean inverted) {
        final TerminalColor color;
        if (invertBackground(flags)) {
            color = bgColor;
        } else {
            color = fgColor;
        }

        if (inverted) {
            return (Color) color.invert().getColor();
        } else {
            return (Color) color.getColor();
        }
    }

    @Override
    public String toString() {
        return Character.toString(character);
    }
}
