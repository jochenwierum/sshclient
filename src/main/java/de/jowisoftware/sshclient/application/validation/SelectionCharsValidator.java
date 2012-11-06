package de.jowisoftware.sshclient.application.validation;

import static de.jowisoftware.sshclient.i18n.Translation.t;
import de.jowisoftware.sshclient.settings.Profile;

public class SelectionCharsValidator implements Validator<Profile<?>> {
    @Override
    public void validate(final Profile<?> profile, final ValidationResult result) {
        if (profile.getGfxSettings().getBoundaryChars() == null) {
            result.addError("gfx.boundaryChars", t("error.gfx.wordBoundary",
                    "word boundary characters are missing"));
        }
    }
}
