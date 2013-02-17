package de.jowisoftware.sshclient.ui.settings.validation;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.application.settings.validation.CharsetValidator;
import de.jowisoftware.sshclient.application.settings.validation.ColorValidator;
import de.jowisoftware.sshclient.application.settings.validation.CursorColorValidator;
import de.jowisoftware.sshclient.application.settings.validation.DefaultValidationResult;
import de.jowisoftware.sshclient.application.settings.validation.EnvironmentValidator;
import de.jowisoftware.sshclient.application.settings.validation.ForwardingsValidator;
import de.jowisoftware.sshclient.application.settings.validation.HostNameValidator;
import de.jowisoftware.sshclient.application.settings.validation.KeepAliveCountValidator;
import de.jowisoftware.sshclient.application.settings.validation.KeepAliveIntervalValidator;
import de.jowisoftware.sshclient.application.settings.validation.PortValidator;
import de.jowisoftware.sshclient.application.settings.validation.SelectionCharsValidator;
import de.jowisoftware.sshclient.application.settings.validation.SocksPortValidator;
import de.jowisoftware.sshclient.application.settings.validation.TimeoutValidator;
import de.jowisoftware.sshclient.application.settings.validation.UserValidator;
import de.jowisoftware.sshclient.application.settings.validation.ValidationResult;
import de.jowisoftware.sshclient.application.settings.validation.ValidatorCollection;
import de.jowisoftware.sshclient.application.settings.validation.X11DisplayValidator;
import de.jowisoftware.sshclient.application.settings.validation.X11HostValidator;

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
