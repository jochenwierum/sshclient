package de.jowisoftware.sshclient.encryption;

public interface EnDeCryptor {
    void setPassword(String password);
    String encrypt(String text) throws CryptoException;
    String decrypt(String encrypted) throws CryptoException;
}
