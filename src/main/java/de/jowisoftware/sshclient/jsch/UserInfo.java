package de.jowisoftware.sshclient.jsch;

import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import de.jowisoftware.sshclient.ui.PasswordDialog;

public class UserInfo implements com.jcraft.jsch.UserInfo {
    private char[] password;
    private char[] passphrase;
    private final JFrame parent;

    public UserInfo(final JFrame parent) {
        this.parent = parent;
    }

    @Override
    public String getPassphrase() {
        final String result = new String(passphrase);
        Arrays.fill(passphrase, (char) 0);
        passphrase = null;
        return result;
    }

    @Override
    public String getPassword() {
        final String result = new String(password);
        Arrays.fill(password, (char) 0);
        password = null;
        return result;
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

    private char[] readPassword(final String message) {
        return new PasswordDialog(parent, message).askPassword();
    }
}
