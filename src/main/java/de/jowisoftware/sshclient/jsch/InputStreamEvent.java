package de.jowisoftware.sshclient.jsch;

public interface InputStreamEvent {
    void gotChars(byte[] buffer, int read);
}