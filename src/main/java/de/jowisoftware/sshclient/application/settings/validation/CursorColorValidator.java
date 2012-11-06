package de.jowisoftware.sshclient.application.settings.validation;

import de.jowisoftware.sshclient.application.settings.Profile;

public class CursorColorValidator<T extends Profile<?>> implements Validator<T> {
    private static final String FIELD = "gfx.cursorcolor";

    @Override
    public void validate(final T profile, final ValidationResult result) {
        if (profile.getGfxSettings().getCursorColor() == null) {
            result.addError(FIELD, "cursor color is missing");
        }
    }
}
