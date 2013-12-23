package de.jowisoftware.sshclient.ui;

import com.jcraft.jsch.JSchException;
import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.filetransfer.FileSystemChildrenProvider;
import de.jowisoftware.sshclient.filetransfer.FileSystemTreeNodeItem;
import de.jowisoftware.sshclient.filetransfer.JSchSftpConnection;
import de.jowisoftware.sshclient.filetransfer.SftpChildrenProvider;
import de.jowisoftware.sshclient.filetransfer.SftpTreeNodeItem;
import de.jowisoftware.sshclient.jsch.SSHUserInfo;
import de.jowisoftware.sshclient.ui.filetransfer.FilePanel;
import de.jowisoftware.sshclient.ui.tabpanel.redrawing.RedrawingTabPanel;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

public class SftpConnectionPanel extends AbstractSSHConnectionPanel<SftpTab> {
    private final AWTProfile profile;
    private JSchSftpConnection connection;

    public SftpConnectionPanel(final AWTProfile profile, final Application application, final RedrawingTabPanel parent, final SftpTab tab) {
        super(application, parent, tab);
        this.profile = profile;
    }

    private void init(final SftpChildrenProvider childrenProvider) {
        final FilePanel<FileSystemTreeNodeItem, FileSystemChildrenProvider> leftPane =
                new FilePanel<>(new FileSystemChildrenProvider());
        final FilePanel<SftpTreeNodeItem, SftpChildrenProvider> rightPane =
                new FilePanel<>(childrenProvider);

        final JPanel buttons = new JPanel();

        setContent(createComponentsPanel(leftPane, buttons, rightPane));
    }

    private JPanel createComponentsPanel(final JComponent leftComponent, final JComponent buttons, final JComponent rightComponent) {
        final JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.weighty = 1;
        constraints.weightx = 1;
        constraints.gridx=0;
        constraints.fill = GridBagConstraints.BOTH;
        result.add(leftComponent, constraints);

        constraints.gridx = 2;
        result.add(rightComponent, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0;
        buttons.setMaximumSize(new Dimension(40, buttons.getMaximumSize().height));
        buttons.setPreferredSize(new Dimension(40, buttons.getPreferredSize().height));
        result.add(buttons, constraints);

        return result;
    }


    @Override
    protected void tryConnect(final SSHUserInfo userInfo) throws JSchException, IOException {
        connection = new JSchSftpConnection(application.jsch, profile, userInfo);
        connection.connect();
        init(connection.getChildrenProvider());
    }

    @Override
    public void takeFocus() {
    }

    @Override
    public void close() {
        removeAll();
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    @Override
    protected void processConnectException(final Exception e) { }
    @Override public void unfreeze() { }
    @Override public void freeze() { }

}
