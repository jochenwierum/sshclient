package de.jowisoftware.sshclient.encryption;

import java.io.*;

class CipherInformation {
    public byte[] salt;
    public byte[] iv;
    public byte[] ciphertext;
    public int keyLength;

    public CipherInformation() {}

    public CipherInformation(final byte[] data) throws CryptoException {
        try (final ByteArrayInputStream bis = new ByteArrayInputStream(data);
                final DataInputStream dis = new DataInputStream(bis)) {
            keyLength = dis.readInt();
            salt = readByteArrayFromStream(dis);
            iv = readByteArrayFromStream(dis);
            ciphertext = readByteArrayFromStream(dis);
        } catch(final IOException e) {
            throw new CryptoException("Error while encoding data", e);
        }
    }

    public byte[] toByteArray() throws CryptoException {
        try (
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                final DataOutputStream dos = new DataOutputStream(bos)) {

            dos.writeInt(keyLength);
            writeByteArrayIntoStream(salt, dos);
            writeByteArrayIntoStream(iv, dos);
            writeByteArrayIntoStream(ciphertext, dos);

            return bos.toByteArray();
        } catch(final IOException e) {
            throw new CryptoException("Error while decoding data", e);
        }
    }

    private void writeByteArrayIntoStream(final byte[] data,
            final DataOutputStream stream) throws IOException {
        stream.writeInt(data.length);
        stream.write(data);
    }

    private byte[] readByteArrayFromStream(final DataInputStream stream)
            throws IOException, CryptoException {
        final int size = stream.readInt();
        final byte[] result = new byte[size];
        final int read = stream.read(result);

        if (read != size) {
            throw new CryptoException("Data is incomplete");
        }
        return result;
    }
}
