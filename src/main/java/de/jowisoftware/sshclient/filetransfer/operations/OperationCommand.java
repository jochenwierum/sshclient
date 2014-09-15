package de.jowisoftware.sshclient.filetransfer.operations;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

public interface OperationCommand {
    long id();
    void execute(ChannelSftp channel, ChannelSftp controlChannel, SftpProgressMonitor monitor) throws SftpException;

    void abort();
    boolean isAborted();
}
