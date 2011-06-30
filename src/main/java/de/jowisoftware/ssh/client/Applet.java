package de.jowisoftware.ssh.client;

public class Applet extends java.applet.Applet {
    private static final long serialVersionUID = -4143101114325971711L;

    @Override
    public void init() {
        super.init();
        new MainWindow();
    }
}
