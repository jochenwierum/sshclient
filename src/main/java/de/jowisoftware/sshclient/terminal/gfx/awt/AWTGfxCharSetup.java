package de.jowisoftware.sshclient.terminal.gfx.awt;

import de.jowisoftware.sshclient.terminal.gfx.*;
import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.charsets.DECCharset;
import de.jowisoftware.sshclient.terminal.charsets.GfxCharset;
import de.jowisoftware.sshclient.terminal.charsets.TerminalCharset;
import de.jowisoftware.sshclient.terminal.charsets.TerminalCharsetSelection;
import de.jowisoftware.sshclient.terminal.charsets.UKCharset;
import de.jowisoftware.sshclient.terminal.charsets.USASCIICharset;

public class AWTGfxCharSetup implements GfxCharSetup {
    private static class CharSetupState implements Cloneable {
        private int attributes;
        private TerminalColor fgColor;
        private TerminalColor bgColor;
        private TerminalCharsetSelection selectedCharset = TerminalCharsetSelection.G0;
        private GfxCharset charsetG0 = USASCIICharset.instance();
        private GfxCharset charsetG1 = USASCIICharset.instance();
        private boolean createBrightColors;

        @Override
        public CharSetupState clone() throws CloneNotSupportedException {
            try {
                return (CharSetupState) super.clone();
            } catch (final CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final Logger LOGGER = Logger
            .getLogger(AWTGfxCharSetup.class);

    private final ColorFactory colorFactory;
    private final AWTGfxInfo gfxInfo;
    private CharSetupState charState = new CharSetupState();
    private CharSetupState savedState = new CharSetupState();


    public AWTGfxCharSetup(final AWTGfxInfo gfxInfo) {
        this.gfxInfo = gfxInfo;
        colorFactory = new AWTColorFactory(gfxInfo);
        reset();
    }

    @Override
    public void reset() {
        charState.createBrightColors = false;
        charState.fgColor = colorFactory.createStandardColor(ColorName.DEFAULT, false);
        charState.bgColor = colorFactory.createStandardColor(ColorName.DEFAULT_BACKGROUND, false);
        charState.attributes = 0;
    }

    @Override
    public void setAttribute(final Attribute attribute) {
        if (attribute.equals(Attribute.BRIGHT)) {
            charState.attributes &= ~Attribute.DIM.flag;
            charState.createBrightColors = true;
        } else if (attribute.equals(Attribute.DIM)) {
            charState.attributes &= ~Attribute.BRIGHT.flag;
            charState.createBrightColors = false;
        } else if (attribute.equals(Attribute.HIDDEN)) {
            charState.fgColor = charState.bgColor;
        }

        charState.attributes |= attribute.flag;

        if (charState.fgColor.name() != null) {
            setForeground(charState.fgColor.name());
        }
    }

    @Override
    public void removeAttribute(final Attribute attribute) {
        if (attribute.equals(Attribute.HIDDEN)) {
            charState.fgColor = colorFactory.createStandardColor(ColorName.DEFAULT, true);
        } else {
            charState.attributes &= ~attribute.flag;
        }
    }

    @Override
    public void setCharset(final TerminalCharsetSelection selection,
            final TerminalCharset newCharset) {

        final GfxCharset charset;

        switch(newCharset) {
        case USASCII:
            charset = USASCIICharset.instance();
            break;
        case DECCHARS:
            charset = DECCharset.instance();
            break;
        case UK:
            charset = UKCharset.instance();
            break;
        default:
            charset = USASCIICharset.instance();
            LOGGER.warn("Switch to unknown charset: " + newCharset);
        }

        if (selection.equals(TerminalCharsetSelection.G0)) {
            this.charState.charsetG0 = charset;
        } else {
            this.charState.charsetG1 = charset;
        }

        LOGGER.info("Charset " + selection + " is now " +
                charset.getClass().getSimpleName());
    }

    @Override
    public void selectCharset(final TerminalCharsetSelection selection) {
        this.charState.selectedCharset = selection;
        LOGGER.info("Switched charset to " + selection);
    }

    @Override
    public AWTGfxChar createChar(final char character) {
        final GfxCharset charset = getCharset();
        final String characterString = Character.toString(
                charset.convertCharacter(character));
        return new AWTGfxChar(characterString,
                gfxInfo, charState.fgColor, charState.bgColor, charState.attributes);
    }

    @Override
    public AWTGfxChar createMultibyteChar(final String characterAsString) {
        return new AWTGfxChar(characterAsString,
                gfxInfo, charState.fgColor, charState.bgColor, charState.attributes);
    }

    private GfxCharset getCharset() {
        if (charState.selectedCharset.equals(TerminalCharsetSelection.G1)) {
            return charState.charsetG1;
        } else {
            return charState.charsetG0;
        }
    }

    @Override
    public AWTGfxChar createClearChar() {
        return new AWTGfxChar(" ", gfxInfo, charState.fgColor, charState.bgColor, 0);
    }

    @Override
    public void setForeground(final ColorName color) {
        charState.fgColor = colorFactory.createStandardColor(color, charState.createBrightColors);
    }

    @Override
    public void setBackground(final ColorName color) {
         charState.bgColor = colorFactory.createStandardColor(color, false);
    }

    @Override
    public void setForeground(final int colorCode) {
        charState.fgColor = colorFactory.createCustomColor(colorCode);
    }

    @Override
    public void setBackground(final int colorCode) {
        charState.bgColor = colorFactory.createCustomColor(colorCode);
    }

    @Override
    public void updateCustomColor(final int colorNumber, final int r,
            final int g, final int b) {
        colorFactory.updateCustomColor(colorNumber, r, g, b);
    }

    @Override
    public void save() {
        try {
            savedState = charState.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void restore() {
        try {
            charState = savedState.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
