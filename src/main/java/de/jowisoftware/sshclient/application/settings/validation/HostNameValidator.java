package de.jowisoftware.sshclient.application.settings.validation;

import de.jowisoftware.sshclient.application.settings.Profile;

import static de.jowisoftware.sshclient.i18n.Translation.t;

public class HostNameValidator implements Validator<Profile<?>> {
    private static final String FIELD = "host";

    @Override
    public void validate(final Profile<?> profile, final ValidationResult result) {
        if (profile.getHost() == null || profile.getHost().equals("")) {
            result.addError(FIELD, t("error.host.empty", "host name is empty"));
        } else if (!profile.getHost().matches("^[A-Za-z0-9._\\[\\]:-]+$")) {
            result.addError(FIELD, t("error.host.format", "host format is not valid"));
        }
    }
}
