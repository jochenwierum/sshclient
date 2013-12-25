package de.jowisoftware.sshclient.ui;

import com.jcraft.jsch.JSchException;
import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.filetransfer.FileSystemChildrenProvider;
import de.jowisoftware.sshclient.filetransfer.FileSystemTreeNodeItem;
import de.jowisoftware.sshclient.filetransfer.JSchSftpConnection;
import de.jowisoftware.sshclient.filetransfer.SftpChildrenProvider;
import de.jowisoftware.sshclient.filetransfer.SftpTreeNodeItem;
import de.jowisoftware.sshclient.filetransfer.operations.OperationCommand;
import de.jowisoftware.sshclient.filetransfer.operations.UploadOperationCommand;
import de.jowisoftware.sshclient.jsch.SSHUserInfo;
import de.jowisoftware.sshclient.ui.filetransfer.FilePanel;
import de.jowisoftware.sshclient.ui.filetransfer.status.StatusPanel;
import de.jowisoftware.sshclient.ui.tabpanel.redrawing.RedrawingTabPanel;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

public class SftpConnectionPanel extends AbstractSSHConnectionPanel<SftpTab> {
    private final AWTProfile profile;
    private JSchSftpConnection connection;
    private FilePanel<FileSystemTreeNodeItem, FileSystemChildrenProvider> leftPane;
    private FilePanel<SftpTreeNodeItem, SftpChildrenProvider> rightPane;
    private StatusPanel statusPanel;

    public SftpConnectionPanel(final AWTProfile profile, final Application application, final RedrawingTabPanel parent, final SftpTab tab) {
        super(application, parent, tab);
        this.profile = profile;
    }

    private void init(final SftpChildrenProvider childrenProvider) {
        leftPane = new FilePanel<>(new FileSystemChildrenProvider());
        rightPane = new FilePanel<>(childrenProvider);
        statusPanel = new StatusPanel(this);

        final JPanel buttons = new JPanel();

        setContent(createComponentsPanel(leftPane, buttons, rightPane, statusPanel));

        test();
    }

    private void test() {
        UploadOperationCommand upl1 = new UploadOperationCommand(1, "c:\\temp\\testfile", "/tmp/testfile");
        UploadOperationCommand upl2 = new UploadOperationCommand(2, "c:\\temp\\testfile1", "/tmp/testfile");
        UploadOperationCommand upl3 = new UploadOperationCommand(3, "c:\\temp\\testfile2", "/tmp/testfile");

        statusPanel.addRow(upl1);
        statusPanel.addRow(upl2);
        statusPanel.addRow(upl3);
        statusPanel.addRow(new UploadOperationCommand(4, "c:\\temp\\testfile", "/tmp/testfile"));
        statusPanel.addRow(new UploadOperationCommand(5, "c:\\temp\\testfile", "/tmp/testfile"));
        statusPanel.addRow(new UploadOperationCommand(6, "c:\\temp\\testfile", "/tmp/testfile"));
        statusPanel.addRow(new UploadOperationCommand(7, "c:\\temp\\testfile", "/tmp/testfile"));
    }

    private JComponent createComponentsPanel(final JComponent leftComponent, final JComponent buttons, final JComponent rightComponent,
            final JComponent bottomComponent) {
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.weighty = 1;
        constraints.weightx = 1;
        constraints.gridx=0;
        constraints.fill = GridBagConstraints.BOTH;
        topPanel.add(leftComponent, constraints);

        constraints.gridx = 2;
        topPanel.add(rightComponent, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0;
        buttons.setMaximumSize(new Dimension(40, buttons.getMaximumSize().height));
        buttons.setPreferredSize(new Dimension(40, buttons.getPreferredSize().height));
        topPanel.add(buttons, constraints);

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
        connection.connect(statusPanel);
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
    }


    public void dequeueAndAbort(final OperationCommand command) {
        if (connection != null) {
            connection.dequeue(command);
        }
    }

    @Override protected void processConnectException(final Exception e) { }
    @Override public void unfreeze() { }
    @Override public void freeze() { }
}
