package de.jowisoftware.sshclient.jsch;

public interface InputStreamEvent {
    void gotChars(byte[] buffer, int read);
    void streamClosed(int exitCode);

    public class InputStreamEventAdapter implements InputStreamEvent {
        @Override
        public void gotChars(final byte[] buffer, final int read) {
        }

        @Override
        public void streamClosed(final int exitCode) {
        }

    }
}