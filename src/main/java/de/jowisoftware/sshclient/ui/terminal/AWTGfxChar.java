package de.jowisoftware.sshclient.ui.terminal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import de.jowisoftware.sshclient.terminal.gfx.Attribute;
import de.jowisoftware.sshclient.terminal.gfx.GfxChar;
import de.jowisoftware.sshclient.terminal.gfx.TerminalColor;

public class AWTGfxChar implements GfxChar {
    private final AWTGfxInfo gfxInfo;
    private final int attributes;
    private final TerminalColor fgColor;
    private final TerminalColor bgColor;
    private final String charAsString;
    private final int charWidth;

    public AWTGfxChar(final String characterAsString,
            final AWTGfxInfo gfxInfo, final TerminalColor fgColor,
            final TerminalColor bgColor,
            final int attributes) {
        if (gfxInfo == null) {
            throw new NullPointerException("gfxInfo is null");
        }
        if (fgColor == null) {
            throw new NullPointerException("fgColor is null");
        }
        if (bgColor == null) {
            throw new NullPointerException("bgColor is null");
        }

        this.gfxInfo = gfxInfo;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
        this.attributes = attributes;
        this.charAsString = characterAsString;
        this.charWidth = characterAsString.toCharArray().length;
    }

    public void drawAt(final Rectangle rect, final int baseLinePos,
            final Graphics g, final int flags) {

        drawBackground(g, rect, flags);
        drawForeground(g, rect, flags, baseLinePos);
    }

    private void drawForeground(final Graphics g, final Rectangle rect,
            final int flags, final int baseLinePos) {
        applyFont(g);
        applyForgroundColor(g, flags);
        drawChar(g, rect, baseLinePos);
    }

    private void drawBackground(final Graphics g, final Rectangle rect,
            final int flags) {
        eraseArea(g, rect, flags);

        if ((flags & RenderFlag.CURSOR.flag) != 0) {
            drawCursor(g, rect, flags);
        }
    }

    private void drawCursor(final Graphics g, final Rectangle rect, final int flags) {
        final Rectangle cursorRect;
        switch (gfxInfo.getCursorStyle()) {
            case Block: cursorRect = rect; break;
            case Underline: cursorRect = new Rectangle(
                    rect.x, rect.y + rect.height - 4, rect.width, 4); break;
            case Horizontal: cursorRect = new Rectangle(
                    rect.x, rect.y, 3, rect.height); break;
            default: throw new RuntimeException("Unsupported cursor style");
        }

        g.setColor(gfxInfo.getCursorColor());
        if ((flags & RenderFlag.FOCUSED.flag) != 0) {
            if ((flags & RenderFlag.BLINKING.flag) != 0 || !gfxInfo.cursorBlinks()) {
                g.fillRect(cursorRect.x, cursorRect.y, cursorRect.width - 1, cursorRect.height - 1);
            }
        } else {
            g.drawRect(cursorRect.x, cursorRect.y, cursorRect.width - 1, cursorRect.height - 1);
        }
    }

    private void eraseArea(final Graphics g, final Rectangle rect,
            final int flags) {
        g.setColor(getBackColor(flags));
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    private void drawChar(final Graphics g,
            final Rectangle rect, final int baseLinePos) {
        final int bottomY = rect.y + baseLinePos;
        g.drawString(charAsString, rect.x, bottomY);

        if ((attributes & Attribute.UNDERSCORE.flag) != 0) {
            g.drawLine(rect.x, bottomY, rect.x + rect.width, bottomY);
        }
    }

    private void applyFont(final Graphics g) {
        if ((attributes & Attribute.BRIGHT.flag) != 0) {
            g.setFont(gfxInfo.getBoldFont());
        } else {
            g.setFont(gfxInfo.getFont());
        }
    }

    private void applyForgroundColor(final Graphics g, final int flags) {
        final boolean blinksNow = (flags & RenderFlag.BLINKING.flag) != 0;
        final boolean canBlink = (attributes & Attribute.BLINK.flag) != 0;
        if (!canBlink || blinksNow) {
            g.setColor(getForeColor(flags));
        } else {
            g.setColor(getBackColor(flags));
        }
    }

    private Color getBackColor(final int flags) {
        return invertColorsIfNeeded(flags, bgColor, fgColor);
    }

    private Color getForeColor(final int flags) {
        return invertColorsIfNeeded(flags, fgColor, bgColor);
    }

    private Color invertColorsIfNeeded(final int flags,
            final TerminalColor defaultColor, final TerminalColor invertedColor) {
        final boolean inversed = (attributes & Attribute.INVERSE.flag) != 0;
        final boolean selected = (flags & RenderFlag.SELECTED.flag) != 0;
        final boolean invertColors = inversed ^ selected;

        final TerminalColor color = invertColors ? invertedColor : defaultColor;

        if ((flags & RenderFlag.INVERTED.flag) != 0) {
            return (Color) color.invert().getColor();
        } else {
            return (Color) color.getColor();
        }
    }

    @Override
    public String toString() {
        return charAsString;
    }

    @Override
    public String getCharAsString() {
        return charAsString;
    }

    @Override
    public int hashCode() {
        return getHash(fgColor, bgColor, attributes, charAsString);
    }

    public static int getHash(final TerminalColor fgColor,
            final TerminalColor bgColor, final int attributes,
            final String charAsString) {
        final int prime = 31;
        int result = 1;
        result = prime * result + attributes;
        result = prime * result + ((bgColor == null) ? 0 : bgColor.hashCode());
        result = prime * result
                + ((charAsString == null) ? 0 : charAsString.hashCode());
        result = prime * result + ((fgColor == null) ? 0 : fgColor.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final AWTGfxChar other = (AWTGfxChar) obj;
        return attributes != other.attributes &&
                bgColor.equals(other.bgColor) &&
                charAsString.equals(other.charAsString) &&
                fgColor.equals(other.fgColor);
    }

    @Override
    public int getCharCount() {
        return charWidth;
    }

    public Color getBackground() {
        return (Color) bgColor.getColor();
    }
}
