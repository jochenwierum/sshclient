package de.jowisoftware.sshclient.application.validation;

import static de.jowisoftware.sshclient.i18n.Translation.t;
import de.jowisoftware.sshclient.terminal.Profile;

public class EnvironmentValidator implements Validator<Profile<?>> {
    private static final String FIELD = "environment";

    @Override
    public void validate(final Profile<?> profile, final ValidationResult result) {
        for (final String key : profile.getEnvironment().keySet()) {
            if (!key.matches("[A-Za-z0-9_]+")) {
                result.addError(FIELD, t("error.environment.name", "illegal environment variable: %s", key));
            }
        }
    }
}
