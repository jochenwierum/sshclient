package de.jowisoftware.sshclient.application.settings.validation;

import de.jowisoftware.sshclient.application.settings.Profile;

import static de.jowisoftware.sshclient.i18n.Translation.t;

public class UserValidator implements Validator<Profile<?>> {
    private static final String FIELD = "user";

    @Override
    public void validate(final Profile<?> profile, final ValidationResult result) {
        if (profile.getUser() == null || profile.getUser().isEmpty()) {
            result.addError(FIELD, t("error.user.empty", "user name is missing"));
        }
    }
}
