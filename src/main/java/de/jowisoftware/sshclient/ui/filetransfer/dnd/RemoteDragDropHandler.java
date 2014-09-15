package de.jowisoftware.sshclient.ui.filetransfer.dnd;

import de.jowisoftware.sshclient.filetransfer.FileInfo;
import de.jowisoftware.sshclient.filetransfer.SftpTreeNodeItem;
import de.jowisoftware.sshclient.filetransfer.operations.OperationCommand;
import de.jowisoftware.sshclient.filetransfer.operations.UploadOperationCommand;
import de.jowisoftware.sshclient.ui.filetransfer.status.OperationQueue;
import de.jowisoftware.sshclient.util.PathUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

public class RemoteDragDropHandler extends AbstractDragDropHelper {
    public RemoteDragDropHandler(final OperationQueue operationQueue) {
        super(operationQueue);
    }

    @Override
    protected SftpTransferInfo getTransferFiles(final List<FileInfo> selectedFiles) {
        return new SftpTransferInfo(selectedFiles, false);
    }

    @Override
    public void doTransfer(final SftpTransferInfo data, final JComponent component) {
        final String targetDir = getTargetDir(component);

        if (data.isLocal()) {
            doRemoteTransfer(data, targetDir);
        } else {
// TODO
        }
    }

    private void doRemoteTransfer(final SftpTransferInfo data, final String targetDir) {
        for (final FileInfo file : data.getFiles()) {
            final OperationCommand operation;
            if (file.isDirectory()) {
// TODO
                throw new UnsupportedOperationException("Not implemented yet");
            } else {
                operation = new UploadOperationCommand(file.getFullName(), PathUtils.concatUnixPathes(targetDir, file.getName()));
            }
            queue(operation);
        }
    }

    public String getTargetDir(final JComponent component) {
        final DefaultMutableTreeNode treeNode = getTargetTreeNode(component);
        return ((SftpTreeNodeItem) treeNode.getUserObject()).getPath();
    }
}
