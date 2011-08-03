package de.jowisoftware.sshclient.settings.validation;

import de.jowisoftware.sshclient.settings.Profile;

public class CharsetValidator implements Validator {

    @Override
    public void validate(final Profile profile, final ValidationResult result) {
        if (profile.getCharset() == null) {
            result.addError("charset", "no charset selected");
        }
    }
}
