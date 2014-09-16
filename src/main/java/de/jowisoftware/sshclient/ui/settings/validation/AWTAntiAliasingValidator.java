package de.jowisoftware.sshclient.ui.settings.validation;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.application.settings.validation.ValidationResult;
import de.jowisoftware.sshclient.application.settings.validation.Validator;
import de.jowisoftware.sshclient.util.FontUtils;

import static de.jowisoftware.sshclient.i18n.Translation.t;

public class AWTAntiAliasingValidator implements Validator<AWTProfile> {
    private static final String FIELD = "gfx.antiAliasingMode";

    @Override
    public void validate(final AWTProfile profile, final ValidationResult result) {
        final int antiAliasingMode = profile.getGfxSettings().getAntiAliasingMode();
        if (antiAliasingMode < 0 || antiAliasingMode > FontUtils.getRenderingHintMap().size() - 1) {
            result.addError(FIELD, t("error.font.anialiasing", "Antialiasing mode is invalid"));
        }
    }
}
