package de.jowisoftware.sshclient.application.settings.validation;

import de.jowisoftware.sshclient.application.settings.Profile;

import static de.jowisoftware.sshclient.i18n.Translation.t;

public class X11DisplayValidator implements Validator<Profile<?>> {
    private static final String FIELD = "x11display";

    @Override
    public void validate(final Profile<?> profile, final ValidationResult result) {
        if (profile.getPort() < 0 || profile.getPort() > Short.MAX_VALUE) {
            result.addError(FIELD, t("error.x11.display", "illegal X11 display"));
        }
    }

}
