package de.jowisoftware.sshclient.jsch;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.jcraft.jsch.UserInfo;

import de.jowisoftware.sshclient.application.PasswordManager;
import de.jowisoftware.sshclient.application.UserAbortException;
import de.jowisoftware.sshclient.util.SwingUtils;

public class SSHUserInfo implements UserInfo {
    private static final int PREFIX_PASSWORD_LENGTH = 13;
    private static final int PREFIX_PASSPHRASE_LENGTH = 15;

    private final JFrame parent;
    private final PasswordManager passwordManager;

    private String password;
    private String passphrase;
    private int yesNoAnswer;

    private boolean isPasswordFirstInput = true;
    private boolean isPassphraseFirstInput = true;

    public SSHUserInfo(final JFrame parent, final PasswordManager passwordManager) {
        this.parent = parent;
        this.passwordManager = passwordManager;
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
        final String passwordId = message.substring(PREFIX_PASSWORD_LENGTH);
        try {
            password = passwordManager.getPassword(passwordId,
                    !isPasswordFirstInput);
        } catch(final UserAbortException e) {
            return false;
        }
        isPasswordFirstInput = false;
        return true;
    }

    @Override
    public boolean promptPassphrase(final String message) {
        final String passwordId = message.substring(PREFIX_PASSPHRASE_LENGTH);
        try {
            passphrase = passwordManager.getPassword(passwordId,
                    !isPassphraseFirstInput);
        } catch(final UserAbortException e) {
            return false;
        }
        isPassphraseFirstInput = false;
        return passphrase != null;
    }

    @Override
    public boolean promptYesNo(final String message) {
        SwingUtils.runInSwingThread(new Runnable() {
            @Override
            public void run() {
                yesNoAnswer = JOptionPane.showConfirmDialog(parent, message);
            }
        });

        return JOptionPane.YES_OPTION == yesNoAnswer;
    }

    @Override
    public void showMessage(final String message) {
        JOptionPane.showMessageDialog(parent, message);
    }
}
