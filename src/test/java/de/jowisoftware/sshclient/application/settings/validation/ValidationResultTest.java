package de.jowisoftware.sshclient.application.settings.validation;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class ValidationResultTest {
    @Test
    public void testAllOk() {
        assertFalse(new DefaultValidationResult().hadErrors());
    }

    @Test
    public void testSingleError() {
        final ValidationResult result = new DefaultValidationResult();
        final String message = "host may not be empty";
        final String field = "host";
        result.addError(field, message);

        assertTrue(result.hadErrors());

        assertEquals(message, result.getErrors().get(field));
    }

    @Test
    public void testMultipleErrorsOnSameField() {
        final ValidationResult result = new DefaultValidationResult();
        final String message = "port must be positive";
        final String field = "port";
        result.addError(field, "dummy message");
        result.addError(field, message);

        assertTrue(result.hadErrors());

        assertEquals(message, result.getErrors().get(field));
    }

    @Test
    public void testMultipleErrors() {
        final ValidationResult result = new DefaultValidationResult();
        final String message1 = "port must be positive";
        final String field1 = "port";
        final String message2 = "host does not exist";
        final String field2 = "host";
        result.addError(field1, message1);
        result.addError(field2, message2);

        assertTrue(result.hadErrors());

        assertEquals(message1, result.getErrors().get(field1));
        assertEquals(message2, result.getErrors().get(field2));
    }
}
