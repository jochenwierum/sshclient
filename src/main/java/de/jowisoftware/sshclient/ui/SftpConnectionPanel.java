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
import de.jowisoftware.sshclient.ui.filetransfer.DirectoryFilePanel;
import de.jowisoftware.sshclient.ui.filetransfer.dnd.LocalDragDropHandler;
import de.jowisoftware.sshclient.ui.filetransfer.dnd.RemoteDragDropHandler;
import de.jowisoftware.sshclient.ui.filetransfer.status.StatusPanel;
import de.jowisoftware.sshclient.ui.tabpanel.redrawing.RedrawingTabPanel;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import java.awt.GridLayout;
import java.io.IOException;

public class SftpConnectionPanel extends AbstractSSHConnectionPanel<SftpTab> {
    private final AWTProfile profile;
    private JSchSftpConnection connection;
    private DirectoryFilePanel<FileSystemTreeNodeItem, FileSystemChildrenProvider> leftPane;
    private DirectoryFilePanel<SftpTreeNodeItem, SftpChildrenProvider> rightPane;
    private StatusPanel statusPanel;

    public SftpConnectionPanel(final AWTProfile profile, final Application application, final RedrawingTabPanel parent, final SftpTab tab) {
        super(application, parent, tab);
        this.profile = profile;
    }

    private void init(final SftpChildrenProvider childrenProvider) {
        final String name = profile.getDefaultTitle();
        statusPanel = new StatusPanel(connection);
        connection.setStatusMonitor(statusPanel);

        leftPane = new DirectoryFilePanel<>(name + " local files",
                new FileSystemChildrenProvider(), new LocalDragDropHandler(statusPanel), statusPanel.getStatusListener());
        rightPane = new DirectoryFilePanel<>(name + " remote files",
                childrenProvider, new RemoteDragDropHandler(statusPanel), statusPanel.getStatusListener());

        setContent(createComponentsPanel(leftPane, rightPane, statusPanel.getComponent()));
    }

    private JComponent createComponentsPanel(final JComponent leftComponent, final JComponent rightComponent,
            final JComponent bottomComponent) {
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 2));
        topPanel.add(leftComponent);
        topPanel.add(rightComponent);

        splitPane.add(topPanel);
        splitPane.add(new JScrollPane(bottomComponent,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
        splitPane.setResizeWeight(0.8);

        return splitPane;
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

        if (leftPane != null) {
            leftPane.close();
            leftPane = null;
        }

        if (rightPane != null) {
            rightPane.close();
            rightPane = null;
        }

        if (statusPanel != null) {
            statusPanel = null;
        }
    }

    @Override protected void processConnectException(final Exception e) { }
    @Override public void unfreeze() { }
    @Override public void freeze() { }
}
