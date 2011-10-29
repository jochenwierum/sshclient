package de.jowisoftware.sshclient.settings.validation;

import de.jowisoftware.sshclient.terminal.Profile;

public interface Validator<T extends Profile<?>> {
    void validate(T profile, ValidationResult result);
}
