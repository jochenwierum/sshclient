package de.jowisoftware.sshclient.application.settings.validation;

import de.jowisoftware.sshclient.application.settings.Profile;

public interface Validator<T extends Profile<?>> {
    void validate(T profile, ValidationResult result);
}
