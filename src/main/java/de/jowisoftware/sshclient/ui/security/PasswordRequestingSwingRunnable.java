package de.jowisoftware.sshclient.ui.security;

import de.jowisoftware.sshclient.application.UserAbortException;
import de.jowisoftware.sshclient.util.SwingUtils;

import javax.swing.*;

public class PasswordRequestingSwingRunnable implements Runnable {
    private PasswordData result;
    private final JFrame parent;
    private final boolean allowSaving;
    private final String message;

    public PasswordRequestingSwingRunnable(final String message,
            final boolean allowSaving, final JFrame parent) {
        this.message = message;
        this.allowSaving = allowSaving;
        this.parent = parent;
    }

    @Override
    public void run() {
        final PasswordDialog dialog = new PasswordDialog(parent, message,
                allowSaving);

        dialog.showDialog();

        result = new PasswordData(charArrayToString(dialog.getPassword()),
                dialog.getSaveFlag());
    }

    private String charArrayToString(final char[] password) {
        if (password == null) {
            return null;
        }
        return new String(password);
    }

    public PasswordData askUser() throws UserAbortException {
        SwingUtils.runInSwingThread(this);

        if (result.password == null) {
            throw new UserAbortException();
        }

        return result;
    }
}
