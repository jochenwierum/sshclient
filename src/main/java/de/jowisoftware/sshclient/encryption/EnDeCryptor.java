package de.jowisoftware.sshclient.encryption;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class EnDeCryptor {
    private static final SecureRandom random = new SecureRandom();
    private static final int WEAK_KEY_LENGTH = 128;
    private static final int WEAK_ITERATION_COUNT = 1024;
    private static final int STRONG_KEY_LENGTH = 256;
    private static final int STRONG_ITERATION_COUNT = 2048;
    private static final int SALT_LENGTH = 8;

    private final SecretKeyFactory factory;
    private boolean strongEncryption;
    private String password;

    public EnDeCryptor() throws NoSuchAlgorithmException {
        factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String encrypt(final String text) throws GeneralSecurityException {
        final byte[] salt = createSalt();
        final Cipher cipher = createEncryptionCipher(salt);

        final byte[] iv = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
        final byte[] ciphertext = cipher.doFinal(text.getBytes(Charset.forName("UTF-8")));
        final byte[] result = joinCipherInformation(salt, iv, ciphertext);
        return new Base64(Integer.MAX_VALUE).encodeAsString(result);
    }

    public String decrypt(final String encrypted) throws GeneralSecurityException {
        final byte[] data = new Base64(Integer.MAX_VALUE).decode(encrypted);
        final byte[] salt = new byte[SALT_LENGTH];
        final byte[] ciphertext;
        final byte[] iv;

        final boolean wasStrongEncryption = data[0] == 1;

        if (wasStrongEncryption) {
            iv = new byte[STRONG_KEY_LENGTH / 8];
        } else {
            iv = new byte[WEAK_KEY_LENGTH / 8];
        }
        ciphertext = new byte[data.length - iv.length - salt.length - 1];

        System.arraycopy(data, 1, salt, 0, salt.length);
        System.arraycopy(data, 1 + SALT_LENGTH, iv, 0, iv.length);
        System.arraycopy(data, 1 + SALT_LENGTH + iv.length, ciphertext, 0, ciphertext.length);

        final Cipher cipher = createDecryptionCipher(salt, iv, wasStrongEncryption);
        return new String(cipher.doFinal(ciphertext), Charset.forName("UTF-8"));
    }

    private byte[] createSalt() {
        final byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    private Cipher createEncryptionCipher(final byte[] salt)
            throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        final SecretKey secret = createSecretKey(salt, strongEncryption);
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        return cipher;
    }

    private Cipher createDecryptionCipher(final byte[] salt,
            final byte[] iv, final boolean wasStrongEncryption)
                    throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        final SecretKey secret = createSecretKey(salt, wasStrongEncryption);
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        return cipher;
    }

    private SecretKey createSecretKey(final byte[] salt, final boolean strongCipher)
            throws GeneralSecurityException {
        final KeySpec spec;
        if (strongCipher) {
            spec = new PBEKeySpec(password.toCharArray(),
                    salt, STRONG_ITERATION_COUNT, STRONG_KEY_LENGTH);
        } else {
            spec = new PBEKeySpec(password.toCharArray(),
                    salt, WEAK_ITERATION_COUNT, WEAK_KEY_LENGTH);
        }

        final SecretKey tmp = factory.generateSecret(spec);
        final SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        return secret;
    }

    private byte[] joinCipherInformation(final byte[] salt, final byte[] iv,
            final byte[] ciphertext) {
        final byte[] result = new byte[iv.length + salt.length + ciphertext.length + 1];

        result[0] = strongEncryption ? (byte) 1 : (byte) 0;

        System.arraycopy(salt, 0, result, 1, salt.length);
        System.arraycopy(iv, 0, result, 1 + salt.length, iv.length);
        System.arraycopy(ciphertext, 0, result, 1 + salt.length + iv.length, ciphertext.length);
        return result;
    }
}
