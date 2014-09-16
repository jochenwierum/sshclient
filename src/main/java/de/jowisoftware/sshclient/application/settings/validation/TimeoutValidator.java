package de.jowisoftware.sshclient.application.settings.validation;

import de.jowisoftware.sshclient.application.settings.Profile;

import static de.jowisoftware.sshclient.i18n.Translation.t;

public class TimeoutValidator implements Validator<Profile<?>> {
    private final static String FIELD = "timeout";

    @Override
    public void validate(final Profile<?> profile, final ValidationResult result) {
        if (profile.getTimeout() < 100) {
            result.addError(FIELD, t("error.timeout.range", "timeout is not in valid range (> 100)"));
        }
    }
}
