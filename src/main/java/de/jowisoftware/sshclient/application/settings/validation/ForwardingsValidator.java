package de.jowisoftware.sshclient.application.settings.validation;

import java.util.List;

import de.jowisoftware.sshclient.application.settings.Forwarding;
import de.jowisoftware.sshclient.application.settings.Profile;

public class ForwardingsValidator implements Validator<Profile<?>> {
    @Override
    public void validate(final Profile<?> profile, final ValidationResult result) {
        validateForwardings(profile.getLocalForwardings(), "localForwardings", result);
        validateForwardings(profile.getRemoteForwardings(), "remoteForwardings", result);
    }

    private void validateForwardings(final List<Forwarding> forwardings,
            final String prefix, final ValidationResult result) {

        for (int i = 0; i < forwardings.size(); ++i) {
            final Forwarding forwarding = forwardings.get(i);

            if (forwarding.remotePort <= 0) {
                result.addError(prefix + "." + i, "error.port.remotePort");
            }
            if (forwarding.sourcePort <= 0) {
                result.addError(prefix + "." + i, "error.port.localPort");
            }
        }
    }
}
