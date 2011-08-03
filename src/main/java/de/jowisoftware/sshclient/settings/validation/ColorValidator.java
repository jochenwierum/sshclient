package de.jowisoftware.sshclient.settings.validation;

import static de.jowisoftware.sshclient.i18n.Translation.t;
import de.jowisoftware.sshclient.settings.Profile;
import de.jowisoftware.sshclient.terminal.Color;

public class ColorValidator implements Validator {
    private static final String COLORFIELD = "gfx.colors";
    private static final String LIGHTCOLORFIELD = "gfx.lightcolors";

    @Override
    public void validate(final Profile profile, final ValidationResult result) {
        for (final Color color : Color.values()) {
            if (profile.getGfxSettings().getColorMap().get(color) == null) {
                result.addError(COLORFIELD, t("error.colors.missing",
                        "missing color: %s", color.name().toLowerCase()));
                break;
            }
        }

        for (final Color color : Color.values()) {
            if (profile.getGfxSettings().getLightColorMap().get(color) == null) {
                result.addError(LIGHTCOLORFIELD, t("error.colors.missing",
                        "missing light color: %s", color.name().toLowerCase()));
                break;
            }
        }
    }
}
