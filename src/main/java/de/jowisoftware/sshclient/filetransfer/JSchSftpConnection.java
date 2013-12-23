package de.jowisoftware.sshclient.filetransfer;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.jsch.SSHUserInfo;

public class JSchSftpConnection {
    private final JSch jsch;
    private final Profile<?> profile;
    private final SSHUserInfo userInfo;
    private Session session;
    private ChannelSftp channel;

    public JSchSftpConnection(final JSch jsch, final Profile<?> profile, final SSHUserInfo userInfo) {
        this.jsch = jsch;
        this.profile = profile;
        this.userInfo = userInfo;
    }

    public void connect() throws JSchException {
        session = jsch.getSession(
                profile.getUser(), profile.getHost(), profile.getPort());
        session.setUserInfo(userInfo);
        session.connect();

        channel = (ChannelSftp)session.openChannel("sftp");
        channel.connect();
    }

    public void close() {
        if (channel != null) {
            channel.disconnect();
            channel = null;
        }

        if (session != null) {
            session.disconnect();
            session = null;
        }
    }

    public SftpChildrenProvider getChildrenProvider() {
        return new SftpChildrenProvider(channel);
    }
}
