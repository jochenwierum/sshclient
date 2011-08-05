package de.jowisoftware.sshclient.ui;

import java.util.HashSet;
import java.util.Set;

import de.jowisoftware.sshclient.terminal.Attribute;
import de.jowisoftware.sshclient.terminal.TerminalColor;
import de.jowisoftware.sshclient.terminal.GfxCharSetup;

public class GfxAwtCharSetup implements GfxCharSetup<GfxAwtChar> {
    private final GfxInfo gfxInfo;
    private final Set<Attribute> attributes = new HashSet<Attribute>();
    private TerminalColor fgColor;
    private TerminalColor bgColor;

    public GfxAwtCharSetup(final GfxInfo gfxInfo) {
        this.gfxInfo = gfxInfo;
        reset();
    }

    @Override
    public void reset() {
        fgColor = TerminalColor.DEFAULT;
        bgColor = TerminalColor.DEFAULTBG;
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
    public void setForeground(final TerminalColor color) {
        fgColor = color;
    }

    @Override
    public void setBackground(final TerminalColor color) {
        bgColor = color;
    }

    @Override
    public GfxAwtChar createChar(final char character) {
        return new GfxAwtChar(character, gfxInfo, fgColor, bgColor,
                attributes);
    }

    @Override
    public void removeAttribute(final Attribute attribute) {
        if (attribute.equals(Attribute.HIDDEN)) {
            fgColor = TerminalColor.DEFAULT;
        } else {
            attributes.remove(attribute);
        }
    }
}
