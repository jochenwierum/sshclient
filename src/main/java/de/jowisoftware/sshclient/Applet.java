package de.jowisoftware.sshclient;

import de.jowisoftware.sshclient.ui.MainWindow;

public class Applet extends java.applet.Applet {
    private static final long serialVersionUID = -4143101114325971711L;

    @SuppressWarnings("unused")
    @Override
    public void init() {
        super.init();
        new MainWindow();
    }
}
