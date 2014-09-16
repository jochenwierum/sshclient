package de.jowisoftware.sshclient.ui.settings.validation;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.application.settings.validation.*;

public final class AWTProfileValidator {
    private final ValidatorCollection<AWTProfile> collection =
            new ValidatorCollection<>();
    private final AWTProfile profile;

    public AWTProfileValidator(final AWTProfile profile) {
        this.profile = profile;

        collection.addValidator(new CharsetValidator());
        collection.addValidator(new ColorValidator<AWTProfile>());
        collection.addValidator(new CursorColorValidator<AWTProfile>());
        collection.addValidator(new EnvironmentValidator());
        collection.addValidator(new ForwardingsValidator());
        collection.addValidator(new HostNameValidator());
        collection.addValidator(new KeepAliveCountValidator());
        collection.addValidator(new KeepAliveIntervalValidator());
        collection.addValidator(new PortValidator());
        collection.addValidator(new SelectionCharsValidator());
        collection.addValidator(new SocksPortValidator());
        collection.addValidator(new TimeoutValidator());
        collection.addValidator(new UserValidator());
        collection.addValidator(new AWTAntiAliasingValidator());
        collection.addValidator(new AWTFontValidator());
        collection.addValidator(new X11DisplayValidator());
        collection.addValidator(new X11HostValidator());

    }

    public ValidationResult validateProfile() {
        final ValidationResult result = new DefaultValidationResult();
        collection.validate(profile, result);
        return result;
    }
}
