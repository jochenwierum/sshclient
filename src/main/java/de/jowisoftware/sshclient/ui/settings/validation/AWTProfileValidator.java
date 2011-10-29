package de.jowisoftware.sshclient.ui.settings.validation;

import de.jowisoftware.sshclient.settings.AWTProfile;
import de.jowisoftware.sshclient.settings.validation.CharsetValidator;
import de.jowisoftware.sshclient.settings.validation.ColorValidator;
import de.jowisoftware.sshclient.settings.validation.CursorColorValidator;
import de.jowisoftware.sshclient.settings.validation.DefaultValidationResult;
import de.jowisoftware.sshclient.settings.validation.EnvironmentValidator;
import de.jowisoftware.sshclient.settings.validation.HostNameValidator;
import de.jowisoftware.sshclient.settings.validation.PortValidator;
import de.jowisoftware.sshclient.settings.validation.UserValidator;
import de.jowisoftware.sshclient.settings.validation.ValidationResult;
import de.jowisoftware.sshclient.settings.validation.ValidatorCollection;

public final class AWTProfileValidator {
    private final ValidatorCollection<AWTProfile> collection =
            new ValidatorCollection<AWTProfile>();
    private final AWTProfile profile;

    public AWTProfileValidator(final AWTProfile profile) {
        this.profile = profile;

        collection.addValidator(new HostNameValidator());
        collection.addValidator(new PortValidator());
        collection.addValidator(new UserValidator());
        collection.addValidator(new CharsetValidator());
        collection.addValidator(new ColorValidator<AWTProfile>());
        collection.addValidator(new CursorColorValidator<AWTProfile>());
        collection.addValidator(new EnvironmentValidator());
        collection.addValidator(new AWTFontValidator());
    }

    public ValidationResult validateProfile() {
        final ValidationResult result = new DefaultValidationResult();
        collection.validate(profile, result);
        return result;
    }
}
