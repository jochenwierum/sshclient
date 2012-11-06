package de.jowisoftware.sshclient.ui.settings.validation;

import de.jowisoftware.sshclient.application.validation.CharsetValidator;
import de.jowisoftware.sshclient.application.validation.ColorValidator;
import de.jowisoftware.sshclient.application.validation.CursorColorValidator;
import de.jowisoftware.sshclient.application.validation.DefaultValidationResult;
import de.jowisoftware.sshclient.application.validation.EnvironmentValidator;
import de.jowisoftware.sshclient.application.validation.HostNameValidator;
import de.jowisoftware.sshclient.application.validation.PortValidator;
import de.jowisoftware.sshclient.application.validation.SelectionCharsValidator;
import de.jowisoftware.sshclient.application.validation.UserValidator;
import de.jowisoftware.sshclient.application.validation.ValidationResult;
import de.jowisoftware.sshclient.application.validation.ValidatorCollection;
import de.jowisoftware.sshclient.application.validation.X11DisplayValidator;
import de.jowisoftware.sshclient.application.validation.X11HostValidator;
import de.jowisoftware.sshclient.settings.awt.AWTProfile;

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
        collection.addValidator(new AWTAntiAliasingValidator());
        collection.addValidator(new SelectionCharsValidator());
        collection.addValidator(new X11DisplayValidator());
        collection.addValidator(new X11HostValidator());
    }

    public ValidationResult validateProfile() {
        final ValidationResult result = new DefaultValidationResult();
        collection.validate(profile, result);
        return result;
    }
}
