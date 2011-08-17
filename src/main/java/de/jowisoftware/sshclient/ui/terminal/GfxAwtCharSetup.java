package de.jowisoftware.sshclient.ui.terminal;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.Attribute;
import de.jowisoftware.sshclient.terminal.GfxCharSetup;
import de.jowisoftware.sshclient.terminal.TerminalCharset;
import de.jowisoftware.sshclient.terminal.TerminalCharsetSelection;
import de.jowisoftware.sshclient.terminal.TerminalColor;
import de.jowisoftware.sshclient.ui.terminal.charsets.DECCharset;
import de.jowisoftware.sshclient.ui.terminal.charsets.UKCharset;
import de.jowisoftware.sshclient.ui.terminal.charsets.USASCIICharset;

public class GfxAwtCharSetup implements GfxCharSetup<GfxAwtChar> {
    private static final Logger LOGGER = Logger
            .getLogger(GfxAwtCharSetup.class);

    private final GfxInfo gfxInfo;
    private final Set<Attribute> attributes = new HashSet<Attribute>();
    private TerminalColor fgColor;
    private TerminalColor bgColor;
    private TerminalCharsetSelection selectedCharset = TerminalCharsetSelection.G0;
    private GfxAwtCharset charsetG0 = new USASCIICharset();
    private GfxAwtCharset charsetG1 = new USASCIICharset();

    private boolean inverseMode;

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

        final GfxAwtCharset charset;

        switch(newCharset) {
        case USASCII:
            charset = new USASCIICharset();
            break;
        case DECCHARS:
            charset = new DECCharset();
            break;
        case UK:
            charset = new UKCharset();
            break;
        default:
            charset = new USASCIICharset();
            LOGGER.warn("Switch to unknown charset: " + newCharset);
        }

        if (selection.equals(TerminalCharsetSelection.G0)) {
            this.charsetG0 = charset;
        } else {
            this.charsetG1 = charset;
        }

        LOGGER.info("Charset " + selection + " is now " +
                charset.getClass().getSimpleName());
    }

    @Override
    public void selectCharset(final TerminalCharsetSelection selection) {
        this.selectedCharset = selection;
        LOGGER.info("Switched charset to " + selection + " ("
                + selectedCharset.getClass().getSimpleName() + ")");
    }

    @Override
    public GfxAwtChar createChar(final char character) {
        return new GfxAwtChar(character, getCharset(selectedCharset),
                gfxInfo, mapColors(fgColor), mapColors(bgColor), attributes);
    }

    private GfxAwtCharset getCharset(final TerminalCharsetSelection selectedCharset2) {
        if (selectedCharset.equals(TerminalCharsetSelection.G1)) {
            return charsetG1;
        } else {
            return charsetG0;
        }
    }

    @Override
    public GfxAwtChar createClearChar() {
        final HashSet<Attribute> newAttributes = new HashSet<Attribute>();
        if (attributes.contains(Attribute.BRIGHT)) {
            newAttributes.add(Attribute.BRIGHT);
        }

        return new GfxAwtChar(' ', new USASCIICharset(),
                gfxInfo, mapColors(fgColor), mapColors(bgColor), newAttributes);
    }

    private TerminalColor mapColors(final TerminalColor color) {
        if (inverseMode && color.equals(TerminalColor.DEFAULT)) {
            return TerminalColor.DEFAULTBG;
        } else if (inverseMode && color.equals(TerminalColor.DEFAULTBG)) {
            return TerminalColor.DEFAULT;
        }
        return color;
    }

    @Override
    public void setInverseMode(final boolean inverseMode) {
        this.inverseMode = inverseMode;
    }
}
