package de.jowisoftware.sshclient.application.settings.validation;

import de.jowisoftware.sshclient.application.settings.Profile;

import static de.jowisoftware.sshclient.i18n.Translation.t;

public class KeepAliveCountValidator implements Validator<Profile<?>> {
    private final static String FIELD = "keepAliveCount";

    @Override
    public void validate(final Profile<?> profile, final ValidationResult result) {
        if (profile.getKeepAliveCount() < 0) {
            result.addError(FIELD, t("error.keepAliveCount.range",
                    "keep alive count is not in valid range (>= 0)"));
        }
    }
}
