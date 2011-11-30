package de.jowisoftware.sshclient.encryption;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Map;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.encryption.PasswordManager.State;

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

        manager = new PasswordManager(cryptor);
        manager.setCheckString(CHECK_STRING);
    }

    private void unlock(final PasswordManager managerToUnlock,
            final EnDeCryptor includedCryptor) throws CryptoException {
        context.checking(new Expectations() {{
            oneOf(includedCryptor).setPassword(PASSWORD);
            oneOf(includedCryptor).decrypt(CHECK_STRING); will(returnValue("46"));
        }});
        managerToUnlock.unlock(PASSWORD);
    }

    private void unlock() throws CryptoException {
        unlock(manager, cryptor);
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


    private Matcher<String> aNumberString() {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(final Object item) {
                return ((String) item).matches("^[0-9]+$");
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("a numeric string");
            }
        };
    }

    @Test public void
    unlockWithCorrectPassword() throws CryptoException {
        unlock();
        assertThat(manager.getState(), is(equalTo(State.UNLOCKED)));
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

        assertThat(manager.getState(), is(equalTo(State.LOCKED)));
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

        assertThat(manager.getState(), is(equalTo(State.LOCKED)));
    }

    @Test public void
    saveAndRestoreTwoPasswords() throws CryptoException {
        unlock();
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
        unlock();
        manager.lock();
        assertThat(manager.getState(), is(equalTo(State.LOCKED)));
    }

    @Test(expected=CryptoException.class) public void
    restoresRequiresUnlocking() throws CryptoException {
        unlock();
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
        unlock();

        prepareEncrypt("y", "enc-y");
        prepareEncrypt("pw123", "enc-pw123");

        manager.savePassword("x", "y");
        manager.savePassword("y", "pw123");

        final Map<String, String> passwords = manager.exportPasswords();
        assertThat(passwords.size(), is(equalTo(2)));
        assertThat(passwords.get("x"), is(not(equalTo("y"))));
    }

    @Test public void
    deletedPasswordsReturnNull() throws CryptoException {
        unlock();

        prepareEncrypt("y", "enc-y");
        manager.savePassword("x", "y");
        manager.deletePassword("x");

        assertThat(manager.restorePassword("x"), is(nullValue()));
    }

    @Test(expected=CryptoException.class) public void
    deletePasswordsWithUnlockedStorageYieldsException() throws CryptoException {
        manager.deletePassword("x");
    }

    @Test public void
    importExportedPasswords() throws CryptoException {
        unlock();

        prepareEncrypt("y", "enc-y");
        prepareEncrypt("pw123", "enc-pw123");

        manager.savePassword("x", "y");
        manager.savePassword("y", "pw123");

        final Map<String, String> passwords = manager.exportPasswords();

        final PasswordManager manager2 = new PasswordManager(cryptor);
        manager2.setCheckString(CHECK_STRING);
        unlock(manager2, cryptor);
        manager2.importPasswords(passwords);

        prepareDecrypt("enc-y", "y");
        prepareDecrypt("enc-pw123", "pw123");
        assertThat(manager2.restorePassword("x"), is(equalTo("y")));
        assertThat(manager2.restorePassword("y"), is(equalTo("pw123")));
    }

    @Test(expected=CryptoException.class) public void
    changingPasswordWhenUnlockedThrowsException() throws CryptoException {
        manager.changePassword("new password");
    }

    @Test public void
    settingInitialPasswordRequiresNoUnlock() throws CryptoException {
        final PasswordManager manager2 = new PasswordManager(cryptor);

        final Sequence seq = context.sequence("seq");
        context.checking(new Expectations() {{
            oneOf(cryptor).setPassword("new password");
                inSequence(seq);
            oneOf(cryptor).encrypt(with(aNumberString()));
                inSequence(seq);
                will(returnValue("bla"));
            oneOf(cryptor).decrypt("bla"); will(returnValue("23"));
        }});

        assertThat(manager2.getState(), is(equalTo(State.UNINITIALIZED)));
        manager2.changePassword("new password");
        assertThat(manager2.getCheckString(), is(equalTo("bla")));
        assertThat(manager2.getState(), is(equalTo(State.UNLOCKED)));
    }

    @Test public void
    changingPasswordRecryptsPasswords() throws CryptoException {
        unlock();

        prepareEncrypt("pw1", "enc-pw1");
        prepareEncrypt("pw2", "enc-pw2");

        manager.savePassword("x", "pw1");
        manager.savePassword("y", "pw2");

        final States pwState = context.states("pwState");
        context.checking(new Expectations() {{
            oneOf(cryptor).decrypt("enc-pw1");
                when(pwState.isNot("new-pw"));
                will(returnValue("dec-pw1"));
            oneOf(cryptor).decrypt("enc-pw2");
                when(pwState.isNot("new-pw"));
                will(returnValue("dec-pw2"));

            oneOf(cryptor).setPassword("new password");
                then(pwState.is("new-pw"));

            oneOf(cryptor).encrypt("dec-pw1");
                when(pwState.is("new-pw"));
                will(returnValue("new-pw1"));
            oneOf(cryptor).encrypt("dec-pw2");
                when(pwState.is("new-pw"));
                will(returnValue("new-pw2"));

            oneOf(cryptor).encrypt(with(aNumberString()));
                when(pwState.is("new-pw"));
                will(returnValue("new-check"));
            oneOf(cryptor).decrypt("new-check");
                when(pwState.is("new-pw"));
                will(returnValue("46"));
        }});

        manager.changePassword("new password");

        final Map<String, String> passwords = manager.exportPasswords();
        assertThat(passwords.get("x"), is(equalTo("new-pw1")));
        assertThat(passwords.get("y"), is(equalTo("new-pw2")));
        assertThat(manager.getCheckString(), is(equalTo("new-check")));
    }

    @Test public void
    errorsWhenChangingPasswordDontTouchPasswords() throws CryptoException {
        unlock();

        prepareEncrypt("pw1", "enc-pw1");
        prepareEncrypt("pw2", "enc-pw2");

        manager.savePassword("x", "pw1");
        manager.savePassword("y", "pw2");

        final States firstState = context.states("firstState").startsAs("first");
        context.checking(new Expectations() {{
            oneOf(cryptor).decrypt("enc-pw1");
                will(returnValue("dec-pw1"));
            oneOf(cryptor).decrypt("enc-pw2");
                will(returnValue("dec-pw2"));

            oneOf(cryptor).setPassword("new password");
            oneOf(cryptor).encrypt(with(aNumberString()));
                will(returnValue("new-check"));
            oneOf(cryptor).decrypt("new-check"); will(returnValue("23"));

            // ignore order

            allowing(cryptor).encrypt("dec-pw1");
                when(firstState.is("first"));
                then(firstState.is("pw1-set"));
                will(returnValue("new-pw1"));
            allowing(cryptor).encrypt("dec-pw2");
                when(firstState.is("pw1-set"));
                will(throwException(new CryptoException("")));

            allowing(cryptor).encrypt("dec-pw2");
                when(firstState.is("first"));
                then(firstState.is("pw2-set"));
                will(returnValue("new-pw2"));
            allowing(cryptor).encrypt("dec-pw1");
                when(firstState.is("pw2-set"));
                will(throwException(new CryptoException("")));
        }});

        try {
            manager.changePassword("new password");
            fail("Exception was not forwarded");
        } catch(final CryptoException e) {}

        final Map<String, String> passwords = manager.exportPasswords();
        assertThat(passwords.get("x"), is(equalTo("enc-pw1")));
    }

    @Test public void
    checkStringIsNullWithoutPassword() {
        final String checkString = new PasswordManager(null).getCheckString();
        assertThat(checkString, is(nullValue()));
    }
}
