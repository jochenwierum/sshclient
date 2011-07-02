package de.jowisoftware.sshclient.jsch;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import de.jowisoftware.sshclient.ui.PasswordDialog;

public class UserInfo implements com.jcraft.jsch.UserInfo {
    private String password;
    private String passphrase;
    private final JFrame parent;

    public UserInfo(final JFrame parent) {
        this.parent = parent;
    }

    @Override
    public String getPassphrase() {
        return passphrase;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean promptPassword(final String message) {
        password = readPassword(message);
        return password != null;
    }

    @Override
    public boolean promptPassphrase(final String message) {
        passphrase = readPassword(message);
        return passphrase != null;
    }

    @Override
    public boolean promptYesNo(final String message) {
        return JOptionPane.showConfirmDialog(parent, message) == JOptionPane.YES_OPTION;
    }

    @Override
    public void showMessage(final String message) {
        JOptionPane.showMessageDialog(parent, message);
    }

    private String readPassword(final String message) {
        return new PasswordDialog(parent, message).askPassword();
    }
}
