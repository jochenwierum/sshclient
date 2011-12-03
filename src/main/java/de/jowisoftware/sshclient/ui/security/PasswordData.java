package de.jowisoftware.sshclient.ui.security;

class PasswordData {
    public final boolean save;
    public final String password;

    public PasswordData(final String password, final boolean savePassword) {
        this.save = savePassword;
        this.password = password;
    }

}
