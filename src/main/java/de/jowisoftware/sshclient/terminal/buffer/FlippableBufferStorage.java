package de.jowisoftware.sshclient.terminal.buffer;

interface FlippableBufferStorage extends BufferStorage {
    public void flipTo(final BufferSelection selection);
    BufferSelection getSelectedStorage();
}
