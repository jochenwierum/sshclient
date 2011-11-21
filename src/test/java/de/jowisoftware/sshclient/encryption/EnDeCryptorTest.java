package de.jowisoftware.sshclient.encryption;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class EnDeCryptorTest {
    @Test
    public void encryptDecryptIsIdempotent() throws Exception {
        final String password = "secret password";
        final String text = "Lorem Ipsum";

        final EnDeCryptor encrypt = new EnDeCryptor();
        encrypt.setPassword(password);

        final EnDeCryptor decrypt = new EnDeCryptor();
        decrypt.setPassword(password);

        final String encrypted = encrypt.encrypt(text);
        assertThat(encrypted, not(is(equalTo(text))));

        final String decrypted = decrypt.decrypt(encrypted);
        assertThat(decrypted, is(equalTo(text)));
    }

    @Test
    public void encryptDecryptIsIdempotentWithSameObject() throws Exception {
        final String password = "a password";
        final String text = "My text";

        final EnDeCryptor crypt = new EnDeCryptor();
        crypt.setPassword(password);
        final String decrypted = crypt.decrypt(crypt.encrypt(text));
        assertThat(decrypted, is(equalTo(text)));
    }
}
