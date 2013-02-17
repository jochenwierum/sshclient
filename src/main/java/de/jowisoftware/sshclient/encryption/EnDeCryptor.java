package de.jowisoftware.sshclient.encryption;

interface EnDeCryptor {
    void setPassword(String password);
    String encrypt(String text) throws CryptoException;
    String decrypt(String encrypted) throws CryptoException;
}
