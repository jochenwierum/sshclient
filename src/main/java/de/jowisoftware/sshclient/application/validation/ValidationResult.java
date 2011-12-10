package de.jowisoftware.sshclient.application.validation;

import java.util.Map;

public interface ValidationResult {

    boolean hadErrors();

    void addError(final String field, final String message);

    Map<String, String> getErrors();

}