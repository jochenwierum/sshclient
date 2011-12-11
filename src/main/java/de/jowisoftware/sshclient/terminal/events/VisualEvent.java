package de.jowisoftware.sshclient.terminal.events;

public interface VisualEvent {
    void bell();
    void newTitle(String title);
    void setDisplayType(DisplayType displayType);
    void newInverseMode(boolean active);

    public class VisualEventAdapter implements VisualEvent {
        @Override
        public void bell() {
        }

        @Override
        public void newTitle(final String title) {
        }

        @Override
        public void setDisplayType(final DisplayType displayType) {
        }

        @Override
        public void newInverseMode(final boolean active) {
        }
    }
}
