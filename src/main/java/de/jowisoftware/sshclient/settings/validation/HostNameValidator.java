package de.jowisoftware.sshclient.settings.validation;

import static de.jowisoftware.sshclient.i18n.Translation.t;
import de.jowisoftware.sshclient.settings.Profile;

public class HostNameValidator implements Validator {
    private static final String FIELD = "host";

    @Override
    public void validate(final Profile profile, final ValidationResult result) {
        if (profile.getHost() == null || profile.getHost().equals("")) {
            result.addError(FIELD, t("error.host.empty", "host name is empty"));
        } else if (!profile.getHost().matches("^[A-Za-z0-9._\\[\\]:-]+$")) {
            result.addError(FIELD, t("error.host.format", "host format is not valid"));
        }
    }
}
