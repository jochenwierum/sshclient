package de.jowisoftware.sshclient.encryption;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

class JavaStandardEnDeCryptor implements EnDeCryptor {
    private static final String SECRET_KEY_TYPE = "PBKDF2WithHmacSHA1";
    private static final String CYPHER_TYPE = "AES/CBC/PKCS5Padding";
    private static final String ENCRYPTION_TYPE = "AES";
    private static final int ITERATION_COUNT = 65136;
    private static final int SALT_LENGTH = 8;

    private static final SecureRandom random = new SecureRandom();
    private final SecretKeyFactory factory;
    private String password;
    private final int maxKeyLength;

    public JavaStandardEnDeCryptor() throws CryptoException {
        try {
            factory = SecretKeyFactory.getInstance(SECRET_KEY_TYPE);
            maxKeyLength = Math.min(256, Cipher.getMaxAllowedKeyLength(CYPHER_TYPE));
        } catch(final GeneralSecurityException e) {
            throw new CryptoException("Cyphers are not available", e);
        }
    }

    @Override
    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public String encrypt(final String text) throws CryptoException {
        final CipherInformation info = new CipherInformation();

        info.keyLength = maxKeyLength;
        info.salt = createSalt();

        try {
            final Cipher cipher = createEncryptionCipher(info.salt);
            info.iv = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
            info.ciphertext = cipher.doFinal(text.getBytes(Charset.forName("UTF-8")));
        } catch(final GeneralSecurityException e) {
            throw new CryptoException("Could not encrypt text", e);
        }

        return new Base64(0).encodeAsString(info.toByteArray());
    }

    private byte[] createSalt() {
        final byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    private Cipher createEncryptionCipher(final byte[] salt)
            throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(CYPHER_TYPE);
        final SecretKey secret = createSecretKey(salt, maxKeyLength);
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        return cipher;
    }

    @Override
    public String decrypt(final String encrypted) throws CryptoException {
        final byte[] data = new Base64(0).decode(encrypted);

        try {
            final CipherInformation info = new CipherInformation(data);
            final Cipher cipher = createDecryptionCipher(info);
            return new String(cipher.doFinal(info.ciphertext), Charset.forName("UTF-8"));
        } catch(final GeneralSecurityException e) {
            throw new CryptoException("Could not encrypt data " +
                    "(data is corrupt or used encryption is unsupported by java)");
        }
    }

    private Cipher createDecryptionCipher(final CipherInformation info)
            throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(CYPHER_TYPE);
        final SecretKey secret = createSecretKey(info.salt, info.keyLength);
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(info.iv));
        return cipher;
    }

    private SecretKey createSecretKey(final byte[] salt, final int keyLength)
            throws GeneralSecurityException {
        final KeySpec spec;

        spec = new PBEKeySpec(password.toCharArray(),
                salt, ITERATION_COUNT, keyLength);
        final SecretKey tmp = factory.generateSecret(spec);
        final SecretKey secret = new SecretKeySpec(tmp.getEncoded(), ENCRYPTION_TYPE);
        return secret;
    }
}
