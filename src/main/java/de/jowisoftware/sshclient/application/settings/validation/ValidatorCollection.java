package de.jowisoftware.sshclient.application.settings.validation;

import de.jowisoftware.sshclient.application.settings.Profile;

import java.util.ArrayList;
import java.util.List;

public class ValidatorCollection<T extends Profile<?>> implements Validator<T> {
    private final List<Validator<? super T>> validators = new ArrayList<>();

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
