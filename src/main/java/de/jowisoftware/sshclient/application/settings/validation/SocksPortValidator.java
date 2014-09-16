package de.jowisoftware.sshclient.application.settings.validation;

import de.jowisoftware.sshclient.application.settings.Profile;

import static de.jowisoftware.sshclient.i18n.Translation.t;

public class SocksPortValidator implements Validator<Profile<?>> {
    private static final String FIELD = "socksport";

    @Override
    public void validate(final Profile<?> profile, final ValidationResult result) {
        if (profile.getSocksPort() != null && (profile.getSocksPort() <= 0
                || profile.getSocksPort() > Short.MAX_VALUE)) {
            result.addError(FIELD,
                    t("error.socksport.range", "illegal SOCKS 4/5 port"));
        }
    }

}
