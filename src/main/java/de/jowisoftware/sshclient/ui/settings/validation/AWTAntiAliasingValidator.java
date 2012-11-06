package de.jowisoftware.sshclient.ui.settings.validation;

import static de.jowisoftware.sshclient.i18n.Translation.t;
import de.jowisoftware.sshclient.application.validation.ValidationResult;
import de.jowisoftware.sshclient.application.validation.Validator;
import de.jowisoftware.sshclient.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.util.FontUtils;

public class AWTAntiAliasingValidator implements Validator<AWTProfile> {
    public static final String FIELD = "gfx.antiAliasingMode";

    @Override
    public void validate(final AWTProfile profile, final ValidationResult result) {
        final int antiAliasingMode = profile.getGfxSettings().getAntiAliasingMode();
        if (antiAliasingMode < 0 || antiAliasingMode > FontUtils.getRenderingHintMap().size() - 1) {
            result.addError(FIELD, t("error.font.anialiasing", "Antialiasing mode is invalid"));
        }
    }
}
