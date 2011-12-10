package de.jowisoftware.sshclient;


public class SSHApplet extends java.applet.Applet {
    private static final long serialVersionUID = -4143101114325971711L;

    @Override
    public void init() {
        super.init();
        new Init().start();
    }
}
