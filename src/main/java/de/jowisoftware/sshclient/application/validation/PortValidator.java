package de.jowisoftware.sshclient.application.validation;

import static de.jowisoftware.sshclient.i18n.Translation.t;
import de.jowisoftware.sshclient.terminal.Profile;

public class PortValidator implements Validator<Profile<?>> {
    private static final String FIELD = "port";

    @Override
    public void validate(final Profile<?> profile, final ValidationResult result) {
        if (profile.getPort() <= 0 || profile.getPort() > Short.MAX_VALUE) {
            result.addError(FIELD, t("error.port.range", "illegal port"));
        }
    }

}
