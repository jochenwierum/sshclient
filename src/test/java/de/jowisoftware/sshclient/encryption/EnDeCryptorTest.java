package de.jowisoftware.sshclient.encryption;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class EnDeCryptorTest {
    @Test
    public void encryptDecryptIsIdempotent() throws Exception {
        final String password = "secret password";
        final String text = "Lorem Ipsum";

        final EnDeCryptor encrypt = new JavaStandardEnDeCryptor();
        encrypt.setPassword(password);

        final EnDeCryptor decrypt = new JavaStandardEnDeCryptor();
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

        final EnDeCryptor crypt = new JavaStandardEnDeCryptor();
        crypt.setPassword(password);
        final String decrypted = crypt.decrypt(crypt.encrypt(text));
        assertThat(decrypted, is(equalTo(text)));
    }
}
