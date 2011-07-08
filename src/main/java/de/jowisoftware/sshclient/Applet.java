package de.jowisoftware.sshclient;

public class Applet extends java.applet.Applet {
    private static final long serialVersionUID = -4143101114325971711L;

    @SuppressWarnings("unused")
    @Override
    public void init() {
        super.init();
        new MainWindow();
    }
}
