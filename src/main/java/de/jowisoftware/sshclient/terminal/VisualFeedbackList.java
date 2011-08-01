package de.jowisoftware.sshclient.terminal;

import java.util.ArrayList;
import java.util.List;

public class VisualFeedbackList implements VisualFeedback {
    private final List<VisualFeedback> list = new ArrayList<VisualFeedback>();

    public void add(final VisualFeedback feedback) {
        list.add(feedback);
    }

    @Override
    public void bell() {
        for (final VisualFeedback f : list) {
            f.bell();
        }
    }

    @Override
    public void setTitle(final String title) {
        for (final VisualFeedback f : list) {
            f.setTitle(title);
        }
    }

    @Override
    public void setDisplayType(final DisplayType displayType) {
        for (final VisualFeedback f : list) {
            f.setDisplayType(displayType);
        }
    }
}
