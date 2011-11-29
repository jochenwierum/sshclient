package de.jowisoftware.sshclient.encryption;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class PasswordManagerTest {
    private static final String CHECK_STRING = "check";
    private static final String PASSWORD = "secret";

    private final Mockery context = new JUnit4Mockery();
    private EnDeCryptor cryptor;
    private PasswordManager manager;

    @Before
    public void setUp() throws CryptoException {
        cryptor = context.mock(EnDeCryptor.class);

        manager = new PasswordManager(cryptor, CHECK_STRING);
    }

    private void unlock(final PasswordManager managerToUnlock,
            final EnDeCryptor includedCryptor) throws CryptoException {
        context.checking(new Expectations() {{
            oneOf(includedCryptor).setPassword(PASSWORD);
            oneOf(includedCryptor).decrypt(CHECK_STRING); will(returnValue("46"));
        }});
        managerToUnlock.unlock(PASSWORD);
    }

    private void prepareEncrypt(final String input, final String output) throws CryptoException {
        context.checking(new Expectations() {{
            oneOf(cryptor).encrypt(input); will(returnValue(output));
        }});
    }

    private void prepareDecrypt(final String input, final String output) throws CryptoException {
        context.checking(new Expectations() {{
            oneOf(cryptor).decrypt(input); will(returnValue(output));
        }});
    }

    @Test public void
    unlockWithCorrectPassword() throws CryptoException {
        unlock(manager, cryptor);
        assertThat(manager.isLocked(), is(false));
    }

    @Test public void
    unlockingWithWrongButWorkingPasswordThrowsException() throws CryptoException {
        context.checking(new Expectations() {{
            allowing(cryptor).setPassword("secret2");
            allowing(cryptor).decrypt(CHECK_STRING); will(returnValue("25"));
        }});

        try {
            manager.unlock("secret2");
            fail("Expected exception");
        } catch(final CryptoException e) { /* ignored, part of the test */ }

        assertThat(manager.isLocked(), is(true));
    }

    @Test public void
    unlockingWithWrongPasswordThrowsException() throws CryptoException {
        context.checking(new Expectations() {{
            allowing(cryptor).setPassword("secret3");
            allowing(cryptor).decrypt(CHECK_STRING); will(throwException(new CryptoException("")));
        }});

        try {
            manager.unlock("secret3");
            fail("Expected exception");
        } catch(final CryptoException e) { /* ignored, part of the test */ }

        assertThat(manager.isLocked(), is(true));
    }

    @Test public void
    saveAndRestoreTwoPasswords() throws CryptoException {
        unlock(manager, cryptor);
        prepareEncrypt("password1", "enc-pw1");
        prepareEncrypt("password2", "enc-pw2");
        prepareEncrypt("password3", "enc-pw3");

        manager.savePassword("profile1", "password1");
        manager.savePassword("profile1", "password3");
        manager.savePassword("profile2", "password2");

        prepareDecrypt("enc-pw3", "password3");
        prepareDecrypt("enc-pw2", "password2");

        assertThat(manager.restorePassword("profile1"), is(equalTo("password3")));
        assertThat(manager.restorePassword("profile2"), is(equalTo("password2")));
    }

    @Test(expected=CryptoException.class) public void
    saveRequiresUnlocking() throws CryptoException {
        manager.savePassword("x", "y");
    }

    @Test public void
    managerIsLockable() throws CryptoException {
        unlock(manager, cryptor);
        manager.lock();
        assertThat(manager.isLocked(), is(equalTo(true)));
    }

    @Test(expected=CryptoException.class) public void
    restoresRequiresUnlocking() throws CryptoException {
        unlock(manager, cryptor);
        prepareEncrypt("y", "enc-y");

        manager.savePassword("x", "y");
        manager.lock();
        manager.restorePassword("x");
    }

    @Test public void
    restoreUnknownPasswordWhenUnlockedYieldsNull() throws CryptoException {
        assertThat(manager.restorePassword("x"), is(nullValue()));
    }

    @Test public void
    passwordsAreEncrypted() throws CryptoException {
        unlock(manager, cryptor);

        prepareEncrypt("y", "enc-y");
        prepareEncrypt("pw123", "enc-pw123");

        manager.savePassword("x", "y");
        manager.savePassword("y", "pw123");

        final Map<String, String> passwords = manager.exportPasswords();
        assertThat(passwords.size(), is(equalTo(2)));
        assertThat(passwords.get("x"), is(not(equalTo("y"))));
    }

    // TODO: import passwords, change password
}
