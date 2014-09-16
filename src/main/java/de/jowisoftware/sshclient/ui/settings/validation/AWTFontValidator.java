package de.jowisoftware.sshclient.ui.settings.validation;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.application.settings.validation.ValidationResult;
import de.jowisoftware.sshclient.application.settings.validation.Validator;
import de.jowisoftware.sshclient.util.FontUtils;

import static de.jowisoftware.sshclient.i18n.Translation.t;

public class AWTFontValidator implements Validator<AWTProfile> {
    private static final String FIELD = "gfx.font";

    @Override
    public void validate(final AWTProfile profile, final ValidationResult result) {
        if (!FontUtils.isMonospacedFont(profile.getGfxSettings().getFont())) {
            result.addError(FIELD, t("error.font.wrongfont", "selected font is no monospace font"));
        }
    }

}
