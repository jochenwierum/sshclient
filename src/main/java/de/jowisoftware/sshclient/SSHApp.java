package de.jowisoftware.sshclient;

public final class SSHApp {
    private SSHApp() { /* This class cannot be intantiated */ }

    public static void main(final String[] args) {
        new Init().start(args);
    }
}
