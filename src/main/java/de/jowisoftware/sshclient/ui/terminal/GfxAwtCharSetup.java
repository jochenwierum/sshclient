package de.jowisoftware.sshclient.ui.terminal;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.Attribute;
import de.jowisoftware.sshclient.terminal.GfxCharSetup;
import de.jowisoftware.sshclient.terminal.TerminalCharset;
import de.jowisoftware.sshclient.terminal.TerminalCharsetSelection;
import de.jowisoftware.sshclient.terminal.TerminalColor;
import de.jowisoftware.sshclient.ui.terminal.charsets.USASCIICharset;

public class GfxAwtCharSetup implements GfxCharSetup<GfxAwtChar> {
    private static final Logger LOGGER = Logger
            .getLogger(GfxAwtCharSetup.class);

    private final GfxInfo gfxInfo;
    private final Set<Attribute> attributes = new HashSet<Attribute>();
    private TerminalColor fgColor;
    private TerminalColor bgColor;
    private GfxAwtCharset selectedCharset = new USASCIICharset();
    private GfxAwtCharset charsetG0 = new USASCIICharset();
    private GfxAwtCharset charsetG1 = new USASCIICharset();

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
        return new GfxAwtChar(character, selectedCharset,
                gfxInfo, fgColor, bgColor, attributes);
    }

    @Override
    public void removeAttribute(final Attribute attribute) {
        if (attribute.equals(Attribute.HIDDEN)) {
            fgColor = TerminalColor.DEFAULT;
        } else {
            attributes.remove(attribute);
        }
    }

    @Override
    public void setCharset(final TerminalCharsetSelection selection,
            final TerminalCharset newCharset) {
        switch(newCharset) {
        case USASCII:
            if (selection.equals(TerminalCharsetSelection.G0)) {
                this.charsetG0 = new USASCIICharset();
            } else {
                this.charsetG1 = new USASCIICharset();
            }
            break;
        default:
            if (selection.equals(TerminalCharsetSelection.G0)) {
                this.charsetG0 = new USASCIICharset();
            } else {
                this.charsetG0 = new USASCIICharset();
            }
            LOGGER.warn("Switch to unknown charset: " + newCharset);
        }
    }

    @Override
    public void selectCharset(final TerminalCharsetSelection selection) {
        if (selection.equals(TerminalCharsetSelection.G0)) {
            this.selectedCharset = charsetG0;
        } else if (selection.equals(TerminalCharsetSelection.G1)) {
            this.selectedCharset = charsetG1;
        }
    }
}
