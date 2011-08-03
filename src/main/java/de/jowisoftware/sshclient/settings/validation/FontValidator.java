package de.jowisoftware.sshclient.settings.validation;

import static de.jowisoftware.sshclient.i18n.Translation.t;
import de.jowisoftware.sshclient.settings.Profile;
import de.jowisoftware.sshclient.util.FontUtils;

public class FontValidator implements Validator {
    private static final String FIELD = "gfx.font";

    @Override
    public void validate(final Profile profile, final ValidationResult result) {
        if (profile.getGfxSettings().getFont() == null) {
            result.addError(FIELD, t("error.font.notset", "no font selected"));
        } else if (!FontUtils.isMonospacedFont(profile.getGfxSettings().getFont())) {
            result.addError(FIELD, t("error.font.wrongfont", "selected font is no monospace font"));
        }
    }

}
