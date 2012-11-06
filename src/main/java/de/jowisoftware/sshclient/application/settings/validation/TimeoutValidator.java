package de.jowisoftware.sshclient.application.settings.validation;

import static de.jowisoftware.sshclient.i18n.Translation.t;
import de.jowisoftware.sshclient.application.settings.Profile;

public class TimeoutValidator implements Validator<Profile<?>> {
    private final static String FIELD = "timeout";

    @Override
    public void validate(final Profile<?> profile, final ValidationResult result) {
        if (profile.getTimeout() < 100) {
            result.addError(FIELD, t("error.timeout.range", "timeout is not in valid range (> 100)"));
        }
    }
}
