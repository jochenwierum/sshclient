package de.jowisoftware.sshclient.ui.terminal;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.charsets.DECCharset;
import de.jowisoftware.sshclient.terminal.charsets.GfxCharset;
import de.jowisoftware.sshclient.terminal.charsets.TerminalCharset;
import de.jowisoftware.sshclient.terminal.charsets.TerminalCharsetSelection;
import de.jowisoftware.sshclient.terminal.charsets.UKCharset;
import de.jowisoftware.sshclient.terminal.charsets.USASCIICharset;
import de.jowisoftware.sshclient.terminal.gfx.Attribute;
import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.terminal.gfx.GfxCharSetup;
import de.jowisoftware.sshclient.terminal.gfx.TerminalColor;

public class AWTGfxCharSetup implements GfxCharSetup {
    private static final Logger LOGGER = Logger
            .getLogger(AWTGfxCharSetup.class);

    private final AWTColorFactory colorFactory;
    private final AWTGfxInfo gfxInfo;
    private int attributes;
    private TerminalColor fgColor;
    private TerminalColor bgColor;
    private TerminalCharsetSelection selectedCharset = TerminalCharsetSelection.G0;
    private GfxCharset charsetG0 = new USASCIICharset();
    private GfxCharset charsetG1 = new USASCIICharset();

    private boolean createBrightColors;

    public AWTGfxCharSetup(final AWTGfxInfo gfxInfo) {
        this.gfxInfo = gfxInfo;
        colorFactory = new AWTColorFactory(gfxInfo);
        reset();
    }

    @Override
    public void reset() {
        createBrightColors = false;
        fgColor = colorFactory.createStandardColor(ColorName.DEFAULT, true);
        bgColor = colorFactory.createStandardColor(ColorName.DEFAULT_BACKGROUND, false);
        attributes = 0;
    }

    @Override
    public void setAttribute(final Attribute attribute) {
        if (attribute.equals(Attribute.BRIGHT)) {
            attributes &= ~Attribute.DIM.flag;
            createBrightColors = true;
        } else if (attribute.equals(Attribute.DIM)) {
            attributes &= ~Attribute.BRIGHT.flag;
            createBrightColors = false;
        } else if (attribute.equals(Attribute.HIDDEN)) {
            fgColor = bgColor;
        }

        attributes |= attribute.flag;
    }

    @Override
    public void removeAttribute(final Attribute attribute) {
        if (attribute.equals(Attribute.HIDDEN)) {
            fgColor = colorFactory.createStandardColor(ColorName.DEFAULT, true);
        } else {
            attributes &= ~attribute.flag;
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
        final GfxCharset charset = getCharset(selectedCharset);
        final String characterString = Character.toString(
                charset.convertCharacter(character));
        return new AWTGfxChar(characterString,
                gfxInfo, fgColor, bgColor, attributes);
    }

    @Override
    public AWTGfxChar createMultibyteChar(final String characterAsString) {
        return new AWTGfxChar(characterAsString,
                gfxInfo, fgColor, bgColor, attributes);
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
        return new AWTGfxChar(" ", gfxInfo, fgColor, bgColor, 0);
    }

    @Override
    public void setForeground(final ColorName color) {
        fgColor = colorFactory.createStandardColor(color, createBrightColors);
    }

    @Override
    public void setBackground(final ColorName color) {
         bgColor = colorFactory.createStandardColor(color, false);
    }

    @Override
    public void setForeground(final int colorCode) {
        fgColor = colorFactory.createCustomColor(colorCode);
    }

    @Override
    public void setBackground(final int colorCode) {
        bgColor = colorFactory.createCustomColor(colorCode);
    }

    @Override
    public void updateCustomColor(final int colorNumber, final int r,
            final int g, final int b) {
        colorFactory.updateCustomColor(colorNumber, r, g, b);
    }
}
