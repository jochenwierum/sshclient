package de.jowisoftware.sshclient.ui.settings;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.ui.terminal.AWTGfxInfo;

class ColorPanel extends JPanel {
    private static final long serialVersionUID = 3678836067715154011L;

    private static final String COLORTYPE_DEFAULT = "color";
    private static final String COLORTYPE_LIGHT = "lightcolor";

    private final AWTGfxInfo gfxInfo;

    public ColorPanel(final AWTGfxInfo GfxInfo) {
        this.gfxInfo = GfxInfo;
        setLayout(new GridLayout(gfxInfo.getColorMap().size() + 1, 3, 3, 3));
        addTitles();
        addColors();
    }


    private void addTitles() {
        add(new JLabel());
        add(new JLabel(t("profiles.colors.normal", "normal colors")));
        add(new JLabel(t("profiles.colors.light", "light colors")));
    }

    private void addColors() {
        for (final ColorName color : ColorName.values()) {
            add(new JLabel(colorTranslation(color)));
            add(createColorButton(
                    gfxInfo.getColorMap().get(color),
                    COLORTYPE_DEFAULT + "." + color.name()));
            add(createColorButton(
                    gfxInfo.getLightColorMap().get(color),
                    COLORTYPE_LIGHT + "." + color.name()));
        }
    }

    private JButton createColorButton(final Color initialColor, final String colorName) {
        final JButton button = new AbstractColorButton(initialColor) {
            private static final long serialVersionUID = -28982437717819503L;

            @Override
            void saveColor(final Color newColor, final ActionEvent e) {
                saveColorInProfile(e.getActionCommand(), newColor);
            }
        };
        button.setActionCommand(colorName);
        return button;
    }

    private void saveColorInProfile(final String actionCommand, final Color newColor) {
        final String[] colorTypes = actionCommand.split("\\.");
        final String colorType = colorTypes[0];
        final String colorName = colorTypes[1];

        final AWTGfxInfo gfxSettings = gfxInfo;
        if (COLORTYPE_DEFAULT.equals(colorType)) {
            gfxSettings.getColorMap().put(
                    ColorName.valueOf(colorName), newColor);
        } else if (COLORTYPE_LIGHT.equals(colorType)) {
            gfxSettings.getLightColorMap().put(
                    ColorName.valueOf(colorName), newColor);
        }
    }

    private String colorTranslation(final ColorName color) {
        return t("profiles.color." + color.name(),
                color.niceName().toLowerCase());
    }
}
