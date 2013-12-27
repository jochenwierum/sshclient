package de.jowisoftware.sshclient.filetransfer.operations;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

public class DownloadOperationCommand extends AbstractOperationCommand {
    private final String remote;
    private final String local;

    public DownloadOperationCommand(final long id, final String remote, final String local) {
        super(id);
        this.remote = remote;
        this.local = local;
    }

    @Override
    public void execute(final ChannelSftp channel, final SftpProgressMonitor monitor) throws SftpException {
        channel.get(remote, local, monitor);
    }
}
