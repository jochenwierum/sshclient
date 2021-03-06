package de.jowisoftware.sshclient.application.settings.validation;

import java.util.HashMap;
import java.util.Map;

public class DefaultValidationResult implements ValidationResult {
    private final Map<String, String> errors = new HashMap<>();

    @Override
    public boolean hadErrors() {
        return !errors.isEmpty();
    }

    @Override
    public void addError(final String field, final String message) {
        errors.put(field, message);
    }

    @Override
    public Map<String, String> getErrors() {
        return errors;
    }
}
