package de.jowisoftware.ssh.client.ui;

import java.util.HashSet;
import java.util.Set;

import de.jowisoftware.ssh.client.tty.GfxCharSetup;

public class GfxAwtCharSetup implements GfxCharSetup<GfxAwtChar> {
    private final GfxInfo gfxInfo;
    private final Set<Attributes> attributes = new HashSet<Attributes>();
    private Colors fgColor;
    private Colors bgColor;

    public GfxAwtCharSetup(final GfxInfo gfxInfo) {
        this.gfxInfo = gfxInfo;
        reset();
    }

    @Override
    public void reset() {
        fgColor = Colors.DEFAULT;
        bgColor = Colors.DEFAULTBG;
        attributes.clear();
    }

    @Override
    public void setAttribute(final Attributes attribute) {
        if (attribute.equals(Attributes.BRIGHT)) {
            attributes.remove(Attributes.DIM);
        } else if (attributes.equals(Attributes.DIM)) {
            attributes.remove(Attributes.BRIGHT);
        }

        attributes.add(attribute);
    }

    @Override
    public void setForeground(final Colors color) {
        fgColor = color;
    }

    @Override
    public void setBackground(final Colors color) {
        bgColor = color;
    }

    @Override
    public GfxAwtChar createChar(final char character) {
        return new GfxAwtChar(character, gfxInfo, fgColor, bgColor,
                attributes.toArray(new Attributes[attributes.size()]));
    }
}
