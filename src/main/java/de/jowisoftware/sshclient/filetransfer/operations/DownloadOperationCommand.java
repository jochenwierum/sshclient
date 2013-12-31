package de.jowisoftware.sshclient.filetransfer.operations;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

import java.io.File;

public class DownloadOperationCommand extends AbstractOperationCommand {
    private final String remote;
    private final File local;

    public DownloadOperationCommand(final String remote, final File local) {
        this.remote = remote;
        this.local = local;
    }

    @Override
    public void execute(final ChannelSftp channel, final SftpProgressMonitor monitor) throws SftpException {
        channel.get(remote, local.getAbsolutePath(), monitor);
    }

    @Override
    public String toString() {
        return String.format("Save %s to %s", remote, local.getAbsolutePath());
    }
}
