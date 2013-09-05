package de.jowisoftware.sshclient.ui.settings.profile;

import org.fest.swing.core.GenericTypeMatcher;

import java.awt.Component;

class NthMatcher<T extends Component> extends GenericTypeMatcher<T> {
    private int count;

    public NthMatcher(final Class<T> supportedType, final int count) {
        super(supportedType);
        this.count = count;
    }

    @Override
    protected boolean isMatching(final T component) {
        if (!component.isShowing()) {
            return false;
        }

        return count-- == 0;
    }
}
