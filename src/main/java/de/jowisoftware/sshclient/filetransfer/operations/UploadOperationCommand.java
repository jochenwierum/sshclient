package de.jowisoftware.sshclient.filetransfer.operations;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

public class UploadOperationCommand extends AbstractOperationCommand {
    private final String local;
    private final String remote;

    public UploadOperationCommand(final String local, final String remote) {
        this.local = local;
        this.remote = remote;
    }

    @Override
    public void execute(final ChannelSftp channel, final ChannelSftp controlChannel, final SftpProgressMonitor monitor) throws SftpException {
        channel.put(local, remote, monitor);
    }
}
