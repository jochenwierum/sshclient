package de.jowisoftware.sshclient.util;

import de.jowisoftware.sshclient.settings.Profile;
import de.jowisoftware.sshclient.settings.validation.CharsetValidator;
import de.jowisoftware.sshclient.settings.validation.ColorValidator;
import de.jowisoftware.sshclient.settings.validation.CursorColorValidator;
import de.jowisoftware.sshclient.settings.validation.DefaultValidationResult;
import de.jowisoftware.sshclient.settings.validation.EnvironmentValidator;
import de.jowisoftware.sshclient.settings.validation.FontValidator;
import de.jowisoftware.sshclient.settings.validation.HostNameValidator;
import de.jowisoftware.sshclient.settings.validation.PortValidator;
import de.jowisoftware.sshclient.settings.validation.UserValidator;
import de.jowisoftware.sshclient.settings.validation.ValidationResult;
import de.jowisoftware.sshclient.settings.validation.ValidatorCollection;

public final class ValidationUtils {
    private ValidationUtils() { /* ignored */ }

    public static ValidationResult validateProfile(final Profile profile) {
        final ValidatorCollection collection = new ValidatorCollection();
        final ValidationResult result = new DefaultValidationResult();

        collection.addValidator(new HostNameValidator());
        collection.addValidator(new PortValidator());
        collection.addValidator(new UserValidator());
        collection.addValidator(new CharsetValidator());
        collection.addValidator(new ColorValidator());
        collection.addValidator(new CursorColorValidator());
        collection.addValidator(new EnvironmentValidator());
        collection.addValidator(new FontValidator());

        collection.validate(profile, result);
        return result;
    }
}
