package de.jowisoftware.sshclient.terminal.buffer;

public interface FlippableBufferStorage extends BufferStorage {
    public void flipTo(final BufferSelection selection);
    public BufferSelection getSelectedStorage();
}
