package de.jowisoftware.sshclient.application.settings.validation;

import de.jowisoftware.sshclient.application.settings.Profile;

public class CharsetValidator implements Validator<Profile<?>> {

    @Override
    public void validate(final Profile<?> profile, final ValidationResult result) {
        if (profile.getCharset() == null) {
            result.addError("charset", "no charset selected");
        }
    }
}
