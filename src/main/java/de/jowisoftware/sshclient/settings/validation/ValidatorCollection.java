package de.jowisoftware.sshclient.settings.validation;

import java.util.ArrayList;
import java.util.List;

import de.jowisoftware.sshclient.settings.Profile;

public class ValidatorCollection implements Validator {
    private final List<Validator> validators = new ArrayList<Validator>();

    @Override
    public void validate(final Profile profile, final ValidationResult result) {
        for (final Validator validator : validators) {
            validator.validate(profile, result);
        }
    }

    public void addValidator(final Validator validator) {
        validators.add(validator);
    }
}
