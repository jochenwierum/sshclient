package de.jowisoftware.sshclient.application.settings.validation;

import java.util.ArrayList;
import java.util.List;

import de.jowisoftware.sshclient.application.settings.Profile;

public class ValidatorCollection<T extends Profile<?>> implements Validator<T> {
    private final List<Validator<? super T>> validators = new ArrayList<Validator<? super T>>();

    @Override
    public void validate(final T profile, final ValidationResult result) {
        for (final Validator<? super T> validator : validators) {
            validator.validate(profile, result);
        }
    }

    public void addValidator(final Validator<? super T> validator) {
        validators.add(validator);
    }
}
