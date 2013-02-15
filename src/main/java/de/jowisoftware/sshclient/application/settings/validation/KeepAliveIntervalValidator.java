package de.jowisoftware.sshclient.application.settings.validation;

import static de.jowisoftware.sshclient.i18n.Translation.t;
import de.jowisoftware.sshclient.application.settings.Profile;

public class KeepAliveIntervalValidator implements Validator<Profile<?>> {
    private final static String FIELD = "keepAliveInterval";

    @Override
    public void validate(final Profile<?> profile, final ValidationResult result) {
        if (profile.getKeepAliveInterval() < 100) {
            result.addError(FIELD, t("error.keepaliveinterval.range",
                            "keep alive interval is not in valid range (> 100)"));
        }
    }
}
