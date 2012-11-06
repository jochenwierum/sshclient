package de.jowisoftware.sshclient.application.validation;

import de.jowisoftware.sshclient.settings.Profile;

public interface Validator<T extends Profile<?>> {
    void validate(T profile, ValidationResult result);
}
