package de.jowisoftware.sshclient.application.validation;

import static de.jowisoftware.sshclient.i18n.Translation.t;
import de.jowisoftware.sshclient.terminal.Profile;
import de.jowisoftware.sshclient.terminal.gfx.ColorName;

public class ColorValidator<T extends Profile<?>> implements Validator<T> {
    private static final String COLORFIELD = "gfx.colors";
    private static final String LIGHTCOLORFIELD = "gfx.lightcolors";

    @Override
    public void validate(final T profile, final ValidationResult result) {
        for (final ColorName color : ColorName.values()) {
            if (profile.getGfxSettings().getColorMap().get(color) == null) {
                result.addError(COLORFIELD, t("error.colors.missing",
                        "missing color: %s", color.name().toLowerCase()));
                break;
            }
        }

        for (final ColorName color : ColorName.values()) {
            if (profile.getGfxSettings().getLightColorMap().get(color) == null) {
                result.addError(LIGHTCOLORFIELD, t("error.colors.missing",
                        "missing color: %s", color.name().toLowerCase()));
                break;
            }
        }
    }
}
