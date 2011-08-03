package de.jowisoftware.sshclient.settings.validation;

import de.jowisoftware.sshclient.settings.Profile;

public class CursorColorValidator implements Validator {
    private static final String FIELD = "gfx.cursorcolor";

    @Override
    public void validate(final Profile profile, final ValidationResult result) {
        if (profile.getGfxSettings().getCursorColor() == null) {
            result.addError(FIELD, "cursor color is missing");
        }
    }
}
