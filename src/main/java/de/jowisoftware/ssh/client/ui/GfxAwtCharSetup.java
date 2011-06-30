package de.jowisoftware.ssh.client.ui;

import java.util.HashSet;
import java.util.Set;

import de.jowisoftware.ssh.client.terminal.Attribute;
import de.jowisoftware.ssh.client.terminal.Color;
import de.jowisoftware.ssh.client.terminal.GfxCharSetup;

public class GfxAwtCharSetup implements GfxCharSetup<GfxAwtChar> {
    private final GfxInfo gfxInfo;
    private final Set<Attribute> attributes = new HashSet<Attribute>();
    private Color fgColor;
    private Color bgColor;

    public GfxAwtCharSetup(final GfxInfo gfxInfo) {
        this.gfxInfo = gfxInfo;
        reset();
    }

    @Override
    public void reset() {
        fgColor = Color.DEFAULT;
        bgColor = Color.DEFAULTBG;
        attributes.clear();
    }

    @Override
    public void setAttribute(final Attribute attribute) {
        if (attribute.equals(Attribute.BRIGHT)) {
            attributes.remove(Attribute.DIM);
        } else if (attributes.equals(Attribute.DIM)) {
            attributes.remove(Attribute.BRIGHT);
        } else if (attributes.equals(Attribute.HIDDEN)) {
            fgColor = bgColor;
        }

        attributes.add(attribute);
    }

    @Override
    public void setForeground(final Color color) {
        fgColor = color;
    }

    @Override
    public void setBackground(final Color color) {
        bgColor = color;
    }

    @Override
    public GfxAwtChar createChar(final char character) {
        return new GfxAwtChar(character, gfxInfo, fgColor, bgColor,
                attributes.toArray(new Attribute[attributes.size()]));
    }

    @Override
    public void removeAttribute(final Attribute attribute) {
        if (attribute.equals(Attribute.HIDDEN)) {
            fgColor = Color.DEFAULT;
        } else {
            attributes.remove(attribute);
        }
    }
}
