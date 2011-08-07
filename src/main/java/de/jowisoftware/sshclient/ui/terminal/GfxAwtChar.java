package de.jowisoftware.sshclient.ui.terminal;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Set;

import de.jowisoftware.sshclient.terminal.Attribute;
import de.jowisoftware.sshclient.terminal.TerminalColor;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;

public class GfxAwtChar implements GfxChar {
    private final GfxInfo gfxInfo;
    private final Attribute[] attributes;
    private final TerminalColor fgColor;
    private final TerminalColor bgColor;
    private final char character;
    private final GfxAwtCharset charset;

    public GfxAwtChar(final char character,
            final GfxAwtCharset charset, final GfxInfo gfxInfo,
            final TerminalColor fgColor, final TerminalColor bgColor,
            final Set<Attribute> attributes) {
        this.character = character;
        this.charset = charset;
        this.gfxInfo = gfxInfo;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
        this.attributes = attributes.toArray(new Attribute[attributes.size()]);
    }

    public void drawAt(final int x, final int y, final int w, final Graphics g) {
        if (!hasAttribute(Attribute.BLINK)) {
            g.setColor(getForeColor());
        } else {
            if (blinkIsForeGround()) {
                g.setColor(getForeColor());
            } else {
                g.setColor(getBackColor());
            }
        }
        charset.drawCharacter(g, character, x, y);
        if (hasAttribute(Attribute.UNDERSCORE)) {
            g.drawLine(x, y, x + w, y);
        }
    }

    private boolean blinkIsForeGround() {
        return (System.currentTimeMillis() / 400) % 2 == 0;
    }

    public void drawBackground(final int x, final int y, final int w, final int h, final Graphics2D g) {
        g.setColor(getBackColor());
        g.fillRect(x, y, w, h);
    }

    private java.awt.Color getBackColor() {
        if (!hasAttribute(Attribute.INVERSE)) {
            return gfxInfo.mapColor(bgColor, hasAttribute(Attribute.BRIGHT));
        } else {
            return gfxInfo.mapColor(fgColor, hasAttribute(Attribute.BRIGHT));
        }
    }

    private java.awt.Color getForeColor() {
        if (!hasAttribute(Attribute.INVERSE)) {
            return gfxInfo.mapColor(fgColor, hasAttribute(Attribute.BRIGHT));
        } else {
            return gfxInfo.mapColor(bgColor, hasAttribute(Attribute.BRIGHT));
        }
    }

    private boolean hasAttribute(final Attribute attribute) {
        for (final Attribute attrib : attributes) {
            if (attrib.equals(attribute)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return Character.toString(character);
    }
}
