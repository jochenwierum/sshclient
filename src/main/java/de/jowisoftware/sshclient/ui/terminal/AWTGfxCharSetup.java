package de.jowisoftware.sshclient.ui.terminal;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.charsets.DECCharset;
import de.jowisoftware.sshclient.terminal.charsets.GfxCharset;
import de.jowisoftware.sshclient.terminal.charsets.TerminalCharset;
import de.jowisoftware.sshclient.terminal.charsets.TerminalCharsetSelection;
import de.jowisoftware.sshclient.terminal.charsets.UKCharset;
import de.jowisoftware.sshclient.terminal.charsets.USASCIICharset;
import de.jowisoftware.sshclient.terminal.gfx.Attribute;
import de.jowisoftware.sshclient.terminal.gfx.ColorFactory;
import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.terminal.gfx.GfxCharSetup;
import de.jowisoftware.sshclient.terminal.gfx.TerminalColor;

public class AWTGfxCharSetup implements GfxCharSetup {
    private static final Logger LOGGER = Logger
            .getLogger(AWTGfxCharSetup.class);

    private final AWTColorFactory colorFactory;
    private final AWTGfxInfo gfxInfo;
    private final Set<Attribute> attributes = new HashSet<Attribute>();
    private TerminalColor fgColor;
    private TerminalColor bgColor;
    private TerminalCharsetSelection selectedCharset = TerminalCharsetSelection.G0;
    private GfxCharset charsetG0 = new USASCIICharset();
    private GfxCharset charsetG1 = new USASCIICharset();

    private boolean inverseMode;

    public AWTGfxCharSetup(final AWTGfxInfo gfxInfo) {
        this.gfxInfo = gfxInfo;
        colorFactory = new AWTColorFactory(gfxInfo);
        reset();
    }

    @Override
    public void reset() {
        fgColor = colorFactory.createStandardColor(ColorName.DEFAULT, true);
        bgColor = colorFactory.createStandardColor(ColorName.DEFAULT_BACKGROUND, false);
        attributes.clear();
    }

    @Override
    public void setAttribute(final Attribute attribute) {
        if (attribute.equals(Attribute.BRIGHT)) {
            attributes.remove(Attribute.DIM);
            colorFactory.createBrightColors(true);
        } else if (attribute.equals(Attribute.DIM)) {
            attributes.remove(Attribute.BRIGHT);
            colorFactory.createBrightColors(false);
        } else if (attribute.equals(Attribute.HIDDEN)) {
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
            fgColor = colorFactory.createStandardColor(ColorName.DEFAULT, true);
        } else {
            attributes.remove(attribute);
        }
    }

    @Override
    public void setCharset(final TerminalCharsetSelection selection,
            final TerminalCharset newCharset) {

        final GfxCharset charset;

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
    public AWTGfxChar createChar(final char character) {
        return new AWTGfxChar(character, getCharset(selectedCharset),
                gfxInfo, inverseColorIfWanted(fgColor), inverseColorIfWanted(bgColor), attributes);
    }

    private GfxCharset getCharset(final TerminalCharsetSelection selectedCharset2) {
        if (selectedCharset.equals(TerminalCharsetSelection.G1)) {
            return charsetG1;
        } else {
            return charsetG0;
        }
    }

    @Override
    public AWTGfxChar createClearChar() {
        final HashSet<Attribute> newAttributes = new HashSet<Attribute>();
        if (attributes.contains(Attribute.BRIGHT)) {
            newAttributes.add(Attribute.BRIGHT);
        }

        return new AWTGfxChar(' ', new USASCIICharset(),
                gfxInfo, inverseColorIfWanted(fgColor),
                inverseColorIfWanted(bgColor), newAttributes);
    }

    private TerminalColor inverseColorIfWanted(final TerminalColor color) {
        if (inverseMode && color.isColor(ColorName.DEFAULT)) {
            return colorFactory.createStandardColor(ColorName.DEFAULT_BACKGROUND, color.isForeground());
        } else if (inverseMode && color.isColor(ColorName.DEFAULT_BACKGROUND)) {
            return colorFactory.createStandardColor(ColorName.DEFAULT, color.isForeground());
        }
        return color;
    }

    @Override
    public void setInverseMode(final boolean inverseMode) {
        this.inverseMode = inverseMode;
    }

    @Override
    public ColorFactory getColorFactory() {
        return colorFactory;
    }
}
