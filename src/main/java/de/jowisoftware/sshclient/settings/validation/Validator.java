package de.jowisoftware.sshclient.settings.validation;

import de.jowisoftware.sshclient.settings.Profile;

public interface Validator {
    void validate(Profile profile, ValidationResult result);
}
