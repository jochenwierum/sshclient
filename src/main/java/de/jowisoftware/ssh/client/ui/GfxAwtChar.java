package de.jowisoftware.ssh.client.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import de.jowisoftware.ssh.client.tty.GfxCharSetup.Attributes;
import de.jowisoftware.ssh.client.tty.GfxCharSetup.Colors;

public class GfxAwtChar implements GfxChar {
    private final GfxInfo gfxInfo;
    private final Attributes[] attributes;
    private final Colors fgColor;
    private final Colors bgColor;
    private final char character;

    public GfxAwtChar(final char character,
            final GfxInfo gfxInfo, final Colors fgColor, final Colors bgColor,
            final Attributes[] attributes) {
        this.character = character;
        this.gfxInfo = gfxInfo;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
        this.attributes = attributes;
    }

    public void drawAt(final int x, final int y, final int w, final Graphics g) {
        if (!hasAttribute(Attributes.BLINK)) {
            g.setColor(getForeColor());
        } else {
            if (blinkIsForeGround()) {
                g.setColor(getForeColor());
            } else {
                g.setColor(getBackColor());
            }
        }
        g.drawString(Character.toString(character), x, y);
        if (hasAttribute(Attributes.UNDERSCORE)) {
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

    private Color getBackColor() {
        if (!hasAttribute(Attributes.REVERSE)) {
            return gfxInfo.mapColor(bgColor, hasAttribute(Attributes.BRIGHT));
        } else {
            return gfxInfo.mapColor(fgColor, hasAttribute(Attributes.BRIGHT));
        }
    }

    private Color getForeColor() {
        if (!hasAttribute(Attributes.REVERSE)) {
            return gfxInfo.mapColor(fgColor, hasAttribute(Attributes.BRIGHT));
        } else {
            return gfxInfo.mapColor(bgColor, hasAttribute(Attributes.BRIGHT));
        }
    }

    private boolean hasAttribute(final Attributes attribute) {
        for (final Attributes attrib : attributes) {
            if (attrib.equals(attribute)) {
                return true;
            }
        }
        return false;
    }
}
